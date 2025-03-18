package com.example.japonnews;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class mispostulaciones extends AppCompatActivity {
    private RecyclerView recyclerView;
    private clasifAdapter adapter;
    private List<clasif_modelo> listaPostulaciones;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_postulaciones);

        recyclerView = findViewById(R.id.recyclerViewMisPostulaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPostulaciones = new ArrayList<>();
        adapter = new clasifAdapter(this, listaPostulaciones);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cargarMisPostulaciones();
    }

    private void cargarMisPostulaciones() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("postulaciones")
                .whereEqualTo("id_usuario", userId)
                .orderBy("fecha_postulacion")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaPostulaciones.clear();
                        HashSet<String> idsUnicos = new HashSet<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idPublicacion = document.getString("id_publicacion");
                            if (idPublicacion != null && !idPublicacion.isEmpty()) {
                                if (idsUnicos.add(idPublicacion)) {
                                    Log.d("Firestore", "Cargando postulación: " + idPublicacion);
                                    obtenerDetallesPublicacion(idPublicacion);
                                } else {
                                    Log.d("Firestore", "Postulación duplicada ignorada: " + idPublicacion);
                                }
                            } else {
                                Log.e("Firestore", "Error: id_publicacion es nulo o vacío en documento " + document.getId());
                                db.collection("postulaciones").document(document.getId()).delete();
                            }
                        }

                        if (idsUnicos.isEmpty()) {
                            Log.d("Firestore", "No se encontraron postulaciones para el usuario: " + userId);
                        }

                    } else {
                        Log.e("Firestore", "Error al obtener postulaciones", task.getException());
                        Toast.makeText(this, "Error al cargar postulaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void obtenerDetallesPublicacion(String clasifId) {
        if (clasifId == null || clasifId.isEmpty()) {
            Log.e("Firestore", "ID de publicación nulo o vacío, no se puede obtener detalles.");
            return;
        }

        db.collection("clasificados")
                .document(clasifId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String titulo = document.getString("titulo");
                        String detalle = document.getString("detalle");
                        String tipo = document.getString("tipoPublicacion");
                        String imagen = document.getString("imagen");

                        listaPostulaciones.add(new clasif_modelo(titulo, detalle, imagen, tipo));
                        adapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error obteniendo detalles", e));
}
}
