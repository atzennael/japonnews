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
    private String titulo, detalle, imagen, userId, clasifId, creadorId;

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
                         clasifId = document.getId();
                         creadorId = document.getString("userId");
                    }
                });


        // Botón de Confirmar Postulación
        btnConfirmar.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = auth.getCurrentUser().getUid();
            db.collection("clasificados")
                    .whereEqualTo("titulo", titulo)
                    .whereEqualTo("detalle", detalle)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                            String clasifId = document.getId();
                            String creadorId = document.getString("userId");

                            Map<String, Object> postulacion = new HashMap<>();
                            postulacion.put("id_usuario", userId);
                            postulacion.put("id_publicacion", clasifId);
                            postulacion.put("fecha_publicacion", FieldValue.serverTimestamp());

                            db.collection("postulaciones")
                                    .add(postulacion)
                                    .addOnSuccessListener(documentReference -> {
                                        Log.d("Postulacion", "Postulacion guardada: "
                                                + documentReference.getId());
                                        enviarNotificacion(creadorId, userId);
                                        Toast.makeText(postulation.this, "¡Postulación enviada con éxito!",
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Log.e ("Postulacion", "Se envió con error: ", e));
                        } else {
                            Log.e("Postulacion", "No se encontró publicación en firestore");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Postulacion", "No se encontró publicación en firestore", e));
        });
    }

    private void guardarPostulacion() {
        if (clasifId == null || creadorId == null){
            Toast.makeText(this, "Error: No se pudo obtener la publicación", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> postulacion = new HashMap<>();
        postulacion.put("fecha_postulacion", FieldValue.serverTimestamp());
        postulacion.put("id_clasificado", clasifId);
        postulacion.put("id_usuario", userId);

        db.collection("postulaciones").add(postulacion)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(postulation.this, "Postulación enviada", Toast.LENGTH_SHORT).show();
                    enviarPostulacion();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(postulation.this, "Error al postular", Toast.LENGTH_SHORT).show());
    }
    private void enviarPostulacion(){
        Map<String, Object> mensaje = new HashMap<>();
        mensaje.put("id_destino", creadorId);  // ID del usuario que creó la publicación
        mensaje.put("id_remitente", userId);
        mensaje.put("mensaje", "Un usuario se ha postulado a tu oferta: " + titulo);
        mensaje.put("fecha_envio", FieldValue.serverTimestamp());

        db.collection("mensajes").add(mensaje)
                .addOnSuccessListener(documentReference -> Log.d("Mensaje", "Mensaje enviado al creador"))
                .addOnFailureListener(e -> Log.e("Mensaje", "Error al enviar mensaje", e));
    }
    private void enviarNotificacion(String creadorId, String userId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        String nombrePostulante = documentSnapshot.getString("nombre");

                        Map<String, Object> notificacion = new HashMap<>();
                        notificacion.put("receptor", creadorId);
                        notificacion.put("mensaje", nombrePostulante+" se ha postulado a tu oferta");
                        notificacion.put("fecha", FieldValue.serverTimestamp());

                        db.collection("notificaciones")
                                .add(notificacion)
                                .addOnSuccessListener(documentReference -> Log.d ("Postulacion", "La notificación fue enviada"))
                        .addOnFailureListener(e -> Log.e ("Postulacion", "La notificación no se envió", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener datos del postulante", e));
    }
}
