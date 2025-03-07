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

public class mispublicaciones extends AppCompatActivity {
    private RecyclerView recyclerView;
    private clasifAdapter adapter;
    private List<clasif_modelo> listaPublicaciones;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_publicaciones);

        recyclerView = findViewById(R.id.recyclerViewMisPublicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaPublicaciones = new ArrayList<>();
        adapter = new clasifAdapter(this, listaPublicaciones);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cargarMisPublicaciones();
    }

    private void cargarMisPublicaciones() {
        String usuarioId = auth.getCurrentUser().getUid();

        db.collection("clasificados")
                .whereEqualTo("userId", usuarioId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaPublicaciones.clear();
                        HashSet<String> idsUnicos = new HashSet<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String clasifId = document.getString("clasifId");

                            if (clasifId != null && !clasifId.isEmpty()) {
                                obtenerDetalles(clasifId);
                            } else {
                                Log.e("Firestore", "Error: id_publicacion es nulo o vacío");
                            }
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener postulaciones", task.getException());
                        Toast.makeText(this, "Error al cargar postulaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void obtenerDetalles(String clasifId){
        String usuarioId = auth.getCurrentUser().getUid();

        db.collection("clasificados")
                .whereEqualTo("userId", usuarioId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaPublicaciones.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String titulo = document.getString("titulo");
                            String detalle = document.getString("detalle");
                            String tipo = document.getString("tipoPublicacion");
                            String imagen = document.getString("imagen");

                            listaPublicaciones.add(new clasif_modelo(titulo, detalle, imagen, tipo));
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("Firestore", "Error al obtener publicaciones", task.getException());
                        Toast.makeText(this, "Error al cargar publicaciones", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void eliminarPublicacion(String idPublicacion) {
        db.collection("publicaciones")
                .document(idPublicacion)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                    cargarMisPublicaciones(); // Recargar la lista
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error al eliminar publicación", e);
                });
    }
}
