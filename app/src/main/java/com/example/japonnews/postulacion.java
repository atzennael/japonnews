package com.example.japonnews;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class PostulacionActivity extends AppCompatActivity {
    private TextView textViewTitulo, textViewDetalle, textViewNombre, textViewCorreo, textViewTelefono;
    private ImageView imageViewOferta;
    private Button btnConfirmar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String titulo, detalle, imagen, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postulacion);

        textViewTitulo = findViewById(R.id.textViewTitulo);
        textViewDetalle = findViewById(R.id.textViewDetalle);
        textViewNombre = findViewById(R.id.textViewNombre);
        textViewCorreo = findViewById(R.id.textViewCorreo);
        textViewTelefono = findViewById(R.id.textViewTelefono);
        imageViewOferta = findViewById(R.id.imageViewOferta);
        btnConfirmar = findViewById(R.id.buttonConfirmar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        // Obtener datos de la oferta
        Intent intent = getIntent();
        if (intent != null) {
            titulo = intent.getStringExtra("titulo");
            detalle = intent.getStringExtra("detalle");
            imagen = intent.getStringExtra("imagen");

            textViewTitulo.setText(titulo);
            textViewDetalle.setText(detalle);

            Glide.with(this)
                    .load(imagen)
                    .placeholder(R.drawable.japonnews_logo)
                    .error(R.drawable.error)
                    .into(imageViewOferta);
        }

        // Cargar datos del usuario desde Firestore
        db.collection("usuarios").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String nombre = documentSnapshot.getString("nombre");
                String correo = documentSnapshot.getString("correo");
                String telefono = documentSnapshot.getString("telefono");

                textViewNombre.setText(nombre);
                textViewCorreo.setText(correo);
                textViewTelefono.setText(telefono);
            }
        });

        // Botón de Confirmar Postulación
        btnConfirmar.setOnClickListener(v -> guardarPostulacion());
    }

    private void guardarPostulacion() {
        Map<String, Object> postulacion = new HashMap<>();
        postulacion.put("titulo", titulo);
        postulacion.put("detalle", detalle);
        postulacion.put("imagen", imagen);
        postulacion.put("postulanteID", userID);
        postulacion.put("nombre", textViewNombre.getText().toString());
        postulacion.put("correo", textViewCorreo.getText().toString());
        postulacion.put("telefono", textViewTelefono.getText().toString());

        db.collection("postulaciones").add(postulacion)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(PostulacionActivity.this, "Postulación enviada", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(PostulacionActivity.this, "Error al postular", Toast.LENGTH_SHORT).show());
    }
}

