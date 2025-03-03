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
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class clasificado extends AppCompatActivity {

    private TextView textViewTitulo, textViewDetalle;
    private ImageView imageViewClasificado;
    private Button btnGuardar, btnPostularme;
    private String titulo, detalle, imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificado);

        textViewTitulo = findViewById(R.id.textViewTitulo);
        textViewDetalle = findViewById(R.id.textViewDetalle);
        imageViewClasificado = findViewById(R.id.imageView5);
        btnGuardar = findViewById(R.id.button5);
        btnPostularme = findViewById(R.id.button6);

        // Recibir datos del intent
        Intent intent = getIntent();
        if (intent != null) {
             titulo = intent.getStringExtra("titulo");
             detalle = intent.getStringExtra("detalle");
             imagen = intent.getStringExtra("imagen");
            Log.d("SavedActivity", "Datos recibidos en clasificado: " + titulo + ", "
                    + detalle + ", " + imagen);

            if (titulo == null) titulo = "Sin título";
            if (detalle == null) detalle = "Sin detalles disponibles";

            textViewTitulo.setText(titulo);
            textViewDetalle.setText(detalle);

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
        if (titulo == null || detalle == null || imagen == null) {
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
                || item.getImagen() == null);


    for (clasif_modelo item : listaSaved) {
        if (item.getTitulo().equals(titulo) && item.getDetalle().equals(detalle)
                && item.getImagen().equals(imagen)) {
            Toast.makeText(this, "Ya añadiste esta publicación a tus Guardados", Toast.LENGTH_SHORT).show();
            return;
        }
    }

        clasif_modelo newList = new clasif_modelo(titulo, detalle, imagen);
        listaSaved.add(newList);

        String json=gson.toJson(listaSaved);
        editor.putString("guardados", json);
        editor.apply();

        Log.d("SavedActivity", "Guardado en SharedPreferences: " + json);
        Toast.makeText(this, "La publicación fue guardada", Toast.LENGTH_SHORT).show();
    }
}