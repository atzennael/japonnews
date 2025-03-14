package com.example.japonnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class post_info extends AppCompatActivity {
    private TextView textViewNombrePostulante, textViewCorreoPostulante, textViewTelefonoPostulante, textViewMensajePostulante;
    private ImageView imageViewPerfil;
    private Button buttonContacto;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);

        // Inicializar vistas
        textViewNombrePostulante = findViewById(R.id.textViewNombrePostulante);
        textViewCorreoPostulante = findViewById(R.id.textViewCorreoPostulante);
        textViewTelefonoPostulante = findViewById(R.id.textViewTelefonoPostulante);
        textViewMensajePostulante = findViewById(R.id.textViewMensajePostulante);
        imageViewPerfil = findViewById(R.id.imageViewPerfil);
        buttonContacto = findViewById(R.id.buttonContacto);

        db = FirebaseFirestore.getInstance();

        // Obtener datos desde el Intent
        String idPostulante = getIntent().getStringExtra("id_postulante");
        String mensaje = getIntent().getStringExtra("mensaje");

        Log.d("post_info", "ID Postulante recibido: " + idPostulante);
        Log.d("post_info", "Mensaje recibido: " + mensaje);

        if (mensaje != null && !mensaje.isEmpty()) {
            textViewMensajePostulante.setText(mensaje);
        } else {
            textViewMensajePostulante.setText("Sin mensaje.");
        }

        if (idPostulante != null && !idPostulante.isEmpty()) {
            cargarDatosUsuario(idPostulante);
        } else {
            Toast.makeText(this, "Error: No se encontró el postulante.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Configurar botón para abrir WhatsApp
        buttonContacto.setOnClickListener(v -> {
            String telefono = textViewTelefonoPostulante.getText().toString().trim();
            if (!telefono.isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/+593" + telefono));
                startActivity(intent);
            } else {
                Toast.makeText(this, "No se encontró un número de teléfono.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarDatosPostulante(String idPostulacion) {
        db.collection("postulaciones").document(idPostulacion)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idPostulante = documentSnapshot.getString("id_usuario");
                        String mensaje = documentSnapshot.getString("mensaje");

                        // Validar y mostrar mensaje del postulante
                        if (mensaje != null && !mensaje.isEmpty() && textViewMensajePostulante != null) {
                            textViewMensajePostulante.setText(mensaje);
                        } else {
                            textViewMensajePostulante.setText("Sin mensaje.");
                        }

                        // Si hay un ID de postulante, cargar sus datos
                        if (idPostulante != null && !idPostulante.isEmpty()) {
                            cargarDatosUsuario(idPostulante);
                        }
                    } else {
                        Toast.makeText(this, "No se encontró la postulación.", Toast.LENGTH_SHORT).show();
                        finish(); // Cerrar la actividad si no se encuentra la notificación
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al cargar la notificación", e);
                    Toast.makeText(this, "Error al cargar la notificación.", Toast.LENGTH_SHORT).show();
                });
    }

    private void cargarDatosUsuario(String idPostulante) {
        db.collection("usuarios").document(idPostulante)
                .get()
                .addOnSuccessListener(userSnapshot -> {
                    if (userSnapshot.exists()) {
                        // Obtener datos del postulante
                        String nombre = userSnapshot.getString("nombre");
                        String email = userSnapshot.getString("email");
                        String telefono = userSnapshot.getString("telefono");
                        String imagenPerfil = userSnapshot.getString("fotoPerfil");

                        // Mostrar datos si las vistas están inicializadas
                        if (nombre != null && textViewNombrePostulante != null) {
                            textViewNombrePostulante.setText(nombre);
                        }
                        if (email != null && textViewCorreoPostulante != null) {
                            textViewCorreoPostulante.setText(email);
                        }
                        if (telefono != null && textViewTelefonoPostulante != null) {
                            textViewTelefonoPostulante.setText(telefono);
                        }

                        // Cargar imagen de perfil con Glide (si existe)
                        if (imagenPerfil != null && !imagenPerfil.isEmpty() && imageViewPerfil != null) {
                            Glide.with(this)
                                    .load(imagenPerfil)
                                    .placeholder(R.drawable.japonnews_logo)
                                    .error(R.drawable.error)
                                    .into(imageViewPerfil);
                        } else {
                            imageViewPerfil.setImageResource(R.drawable.japonnews_logo);
                        }
                    } else {
                        Toast.makeText(this, "No se encontraron datos del postulante.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al cargar datos del postulante", e);
                    Toast.makeText(this, "Error al cargar los datos del postulante.", Toast.LENGTH_SHORT).show();
                });
    }
}
