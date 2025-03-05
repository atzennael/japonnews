package com.example.japonnews;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class clasificado extends AppCompatActivity {

    private TextView textViewTitulo, textViewDetalle, tvVacante;
    private ImageView imageViewClasificado;
    private Button btnGuardar, btnPostularme;
    private String titulo, detalle, imagen, tipoPublicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificado);

        textViewTitulo = findViewById(R.id.textViewTitulo);
        textViewDetalle = findViewById(R.id.textViewDetalle);
        imageViewClasificado = findViewById(R.id.imageView5);
        tvVacante=findViewById(R.id.tvVacante);
        btnGuardar = findViewById(R.id.button5);
        btnPostularme = findViewById(R.id.button6);

        btnPostularme.setOnClickListener(v -> {
            Intent intentPostulacion = new Intent(clasificado.this, postulation.class);
            intentPostulacion.putExtra("titulo", titulo);
            intentPostulacion.putExtra("detalle", detalle);
            intentPostulacion.putExtra("imagen", imagen);
            startActivity(intentPostulacion);
            if (titulo.isEmpty()||titulo==null||detalle.isEmpty()||detalle==null||imagen.isEmpty()||imagen==null){
                Log.e("Postulacion", "No se envió" + titulo + imagen + detalle);
            } else {
                Log.d ("Postulacion", "Se envió");
            }
        });


        // Recibir datos del intent
        Intent intent = getIntent();
        if (intent != null) {
             titulo = intent.getStringExtra("titulo");
             detalle = intent.getStringExtra("detalle");
             imagen = intent.getStringExtra("imagen");
             tipoPublicacion = intent.getStringExtra("tipoPublicacion");

            Log.d("SavedActivity", "Datos recibidos en clasificado: " + titulo + ", "
                    + detalle + ", " + imagen);

            if (titulo == null) titulo = "Sin título";
            if (detalle == null) detalle = "Sin detalles disponibles";
            if (tipoPublicacion==null) tipoPublicacion = "Sin detalles disponibles";

            textViewTitulo.setText(titulo);
            textViewDetalle.setText(detalle);
            tvVacante.setText(tipoPublicacion);

            // Cargar imagen desde URL con
            if(imagen!=null&&!imagen.isEmpty()) {
                Glide.with(this)
                        .load(imagen)
                        .placeholder(R.drawable.japonnews_logo) // Imagen temporal mientras carga
                        .error(R.drawable.error) // Imagen si falla la carga
                        .into(imageViewClasificado);
            } else {
                imageViewClasificado.setImageResource(R.drawable.japonnews_logo);
            }
        } else {
            Log.e("ClasificadoActivity", "No se recibieron datos en el Intent");
        }
        btnGuardar.setOnClickListener(v -> {
            guardarClas();
            Log.d("SavedActivity", "Intentando guardar - Título: "
                    + titulo + ", Detalle: " + detalle + ", Imagen: " + imagen);
        });
    }

    private void guardarClas() {
        if (titulo == null || detalle == null || imagen == null || tipoPublicacion==null) {
            Log.e("SavedActivity", "Se intentó guardar sin datos - Título: " + titulo + ", Detalle: " + detalle + ", Imagen: " + imagen);
            return;
        }
        SharedPreferences preferences = getSharedPreferences("Guardados", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        List<clasif_modelo> listaSaved;

        String savedJson = preferences.getString("guardados", null);
        if (savedJson != null) {
            Type type = new TypeToken<List<clasif_modelo>>() {
            }.getType();
            listaSaved = gson.fromJson(savedJson, type);
        } else {
            listaSaved = new ArrayList<>();
        }

        listaSaved.removeIf(item -> item==null ||
                item.getTitulo() == null
                || item.getDetalle() == null
                || item.getImagen() == null
                || item.getTipoPublicacion()== null);


    for (clasif_modelo item : listaSaved) {
        if (item.getTitulo().equals(titulo) && item.getDetalle().equals(detalle)
                && item.getImagen().equals(imagen) && item.getTipoPublicacion().equals(tipoPublicacion)) {
            Toast.makeText(this, "Ya añadiste esta publicación a tus Guardados", Toast.LENGTH_SHORT).show();
            return;
        }
    }

        clasif_modelo newList = new clasif_modelo(titulo, detalle, imagen, tipoPublicacion);
        listaSaved.add(newList);

        String json=gson.toJson(listaSaved);
        editor.putString("guardados", json);
        editor.apply();

        Log.d("SavedActivity", "Guardado en SharedPreferences: " + json);
        Toast.makeText(this, "La publicación fue guardada", Toast.LENGTH_SHORT).show();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();
        db.collection("clasificados")
                .whereEqualTo("titulo", titulo)
                .whereEqualTo("detalle", detalle)
                .whereEqualTo("tipo_publicacion", tipoPublicacion)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        String clasifId = document.getId();

                        DocumentReference ref = db.collection("favoritos").document(userId +"_"+clasifId);

                        Map<String, Object> guardado = new HashMap<>();
                        guardado.put("id_usuario", userId);
                        guardado.put("id_publicacion", clasifId);
                        guardado.put("fecha_publicacion", FieldValue.serverTimestamp());

                        ref.set(guardado)
                                .addOnSuccessListener(aVoid -> Log.d ("Guarado", "Se guardó con éxito"))
                                .addOnFailureListener(e -> Log.e ("Guardado", "No fueron guardados", e));
                    } else {
                        Log.e("Firestore", "No se encontró la publicación en Firestore");
                    }

                })
                .addOnFailureListener(e -> Log.e ("Clasificado", "Error al obtener ClasifId"));


    }
}