package com.example.japonnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class postulation extends AppCompatActivity {
    private TextView textViewTitulo, textViewDetalle, textViewNombre, textViewCorreo, textViewTelefono;
    private ImageView imageViewOferta;
    private Button btnConfirmar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String titulo, detalle, imagen, userId, clasifId,
            creadorId, telefono, idPublicacion;

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
        userId = mAuth.getCurrentUser().getUid();

        // Obtener datos de la oferta
        Intent intent = getIntent();
        if (intent != null) {
            titulo = intent.getStringExtra("titulo");
            detalle = intent.getStringExtra("detalle");
            imagen = intent.getStringExtra("imagen");
            clasifId = intent.getStringExtra("clasifId"); // Obtener clasifId enviado

            Log.d("Firestore", "Recibido clasifId: " + clasifId);

            textViewTitulo.setText(titulo);
            textViewDetalle.setText(detalle);

            Glide.with(this)
                    .load(imagen)
                    .placeholder(R.drawable.japonnews_logo)
                    .error(R.drawable.error)
                    .into(imageViewOferta);
        }
        db.collection("usuarios").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String nombre = documentSnapshot.getString("nombre");
                String email = documentSnapshot.getString("email");
                String telefono = documentSnapshot.getString("telefono");

                textViewNombre.setText(nombre);
                textViewCorreo.setText(email);
                textViewTelefono.setText(telefono);
            }
        });

        db.collection("clasificados").whereEqualTo("titulo", titulo)
                .whereEqualTo("detalle", detalle).limit(1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                         //clasifId = document.getString("clasifId");
                         creadorId = document.getString("userId");
                    }
                });

        btnConfirmar.setOnClickListener(v -> {
            if(clasifId==null){
                obtenerClasificado();
            } else {
                guardarPostulacion(clasifId);
            }
        });
    }

    private void obtenerClasificado(){
        db.collection("clasificados")
                .whereEqualTo("titulo", titulo)
                .whereEqualTo("detalle", detalle)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        //clasifId=documentSnapshot.getId();
                        creadorId = documentSnapshot.getString("userId");
                        guardarPostulacion(clasifId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Postulacion", "Error obteniendo clasifId", e));
    }

    private void guardarPostulacion(String clasifId) {
        String userId = mAuth.getCurrentUser().getUid();

        if (clasifId == null || clasifId.isEmpty()) {
            Log.e("Firestore", "Error: clasifId es nulo o vacío, no se puede guardar la postulación.");
            return;
        }

        // Verificar si el usuario ya se postuló antes de guardar
        db.collection("postulaciones")
                .whereEqualTo("id_usuario", userId)
                .whereEqualTo("id_publicacion", clasifId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d("Firestore", "Ya existe una postulación para esta publicación: " + clasifId);
                        Toast.makeText(this, "Ya te postulaste a esta oferta.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Si no hay postulación, guardamos la nueva
                        Map<String, Object> data = new HashMap<>();
                        data.put("id_usuario", userId);
                        data.put("id_publicacion", clasifId); // Ahora se guarda correctamente
                        data.put("fecha_postulacion", FieldValue.serverTimestamp());

                        db.collection("postulaciones")
                                .add(data)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Firestore", "Postulación guardada correctamente: " + documentReference.getId());
                                    Toast.makeText(this, "Te has postulado correctamente", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error al guardar postulación", e);
                                    Toast.makeText(this, "Error al postularse", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al verificar postulaciones previas", e));
    }
}


