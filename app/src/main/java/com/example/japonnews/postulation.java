package com.example.japonnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import okhttp3.*;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class postulation extends AppCompatActivity {
    private TextView textViewTitulo, textViewDetalle, textViewNombre, textViewCorreo, textViewTelefono;
    private ImageView imageViewOferta;
    private Button btnConfirmar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText mensaje;
    private String titulo, detalle, imagen, userId, clasifId, creadorId;
    private static final String TAG = "Postulación";
    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/TU_PROYECTO/messages:send";
    private static final String ACCESS_TOKEN = "ya29.a0AeXRPp6wP0vRc2d756vyyu_13ev5JLkZm50Ji_dZiAY8fjpqfST2_iE9a8isvEgGicTRvtNaeqIOmnGyuVWaGPlXkeEO5J3ET_2nYyorwWGCyKpBWDjzKsr8raaDXP0ih9_UE1m2Jvk8p4bIzg3qCT21NSAnD11a54o1P3v3aCgYKAbMSARESFQHGX2Mi3dxJT_furHj-1LjvC7PM6g0175";

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
        mensaje = findViewById(R.id.mensaje);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = mAuth.getCurrentUser().getUid();

        // Obtener datos de la oferta
        Intent intent = getIntent();
        if (intent != null) {
            titulo = intent.getStringExtra("titulo");
            detalle = intent.getStringExtra("detalle");
            imagen = intent.getStringExtra("imagen");
            clasifId = intent.getStringExtra("clasifId");

            textViewTitulo.setText(titulo);
            textViewDetalle.setText(detalle);

            Glide.with(this)
                    .load(imagen)
                    .placeholder(R.drawable.japonnews_logo)
                    .error(R.drawable.error)
                    .into(imageViewOferta);
        }

        // Obtener datos del usuario actual
        db.collection("usuarios").document(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                textViewNombre.setText(documentSnapshot.getString("nombre"));
                textViewCorreo.setText(documentSnapshot.getString("email"));
                textViewTelefono.setText(documentSnapshot.getString("telefono"));
            }
        });

        btnConfirmar.setOnClickListener(v -> {
            String mensajeTexto = mensaje.getText().toString().trim();
            if (mensajeTexto.isEmpty()) {
                Toast.makeText(this, "Escribe un mensaje para tu postulación.", Toast.LENGTH_SHORT).show();
                return;
            }

            guardarPostulacion(mensajeTexto);
        });
    }

    private void guardarPostulacion(String mensajeTexto) {
        db.collection("clasificados").document(clasifId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.e(TAG, "No se encontró la publicación.");
                        return;
                    }

                    creadorId = documentSnapshot.getString("userId");
                    if (creadorId == null || creadorId.isEmpty()) {
                        Log.e(TAG, "Error: creadorId es nulo o vacío.");
                        return;
                    }

                    db.collection("postulaciones")
                            .whereEqualTo("id_usuario", userId)
                            .whereEqualTo("id_publicacion", clasifId)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    Toast.makeText(this, "Ya te postulaste a esta oferta.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("id_usuario", userId);
                                    data.put("id_publicacion", clasifId);
                                    data.put("fecha_postulacion", FieldValue.serverTimestamp());
                                    data.put("mensaje", mensajeTexto);

                                    db.collection("postulaciones").add(data)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(this, "Te has postulado correctamente", Toast.LENGTH_SHORT).show();
                                                obtenerTokenFCM(creadorId, mensajeTexto);
                                            })
                                            .addOnFailureListener(e -> Log.e(TAG, "Error al guardar postulación", e));
                                }
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Error al verificar postulaciones previas", e));
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error al obtener la publicación", e));
    }

    private void obtenerTokenFCM(String creadorId, String mensajeTexto) {
        db.collection("usuarios").document(creadorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Log.e(TAG, "No se encontró el usuario en Firestore.");
                        return;
                    }

                    String tokenFCM = documentSnapshot.getString("tokenFCM");
                    if (tokenFCM != null && !tokenFCM.isEmpty()) {
                        enviarNotificacionFCM(tokenFCM, "Nueva postulación en tu oferta", mensajeTexto);
                    } else {
                        Log.e(TAG, "El token FCM es nulo o vacío.");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error obteniendo el token FCM del usuario", e));
    }

    private void enviarNotificacionFCM(String tokenFCM, String titulo, String mensaje) {
        try {
            OkHttpClient client = new OkHttpClient();

            JSONObject notification = new JSONObject();
            notification.put("title", titulo);
            notification.put("body", mensaje);

            JSONObject message = new JSONObject();
            message.put("token", tokenFCM);
            message.put("notification", notification);

            JSONObject requestBody = new JSONObject();
            requestBody.put("message", message);

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"),
                    requestBody.toString()
            );

            Request request = new Request.Builder()
                    .url(FCM_URL)
                    .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Error al enviar la notificación", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d(TAG, "Notificación enviada con éxito: " + response.body().string());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

