package com.example.japonnews;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class VerPostulacionesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostulacionAdapter adapter;
    private List<post_modelo> listaPostulaciones = new ArrayList<>();
    private FirebaseFirestore db;
    private String ofertaID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_postulaciones);

        recyclerView = findViewById(R.id.recyclerViewPostulaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PostulacionAdapter(listaPostulaciones);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        // Obtener el ID de la oferta desde el intent
        ofertaID = getIntent().getStringExtra("ofertaID");

        cargarPostulaciones();
    }

    private void cargarPostulaciones() {
        db.collection("postulaciones")
                .whereEqualTo("titulo", ofertaID)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaPostulaciones.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        post_modelo postulacion = document.toObject(post_modelo.class);
                        listaPostulaciones.add(postulacion);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Toast.makeText(VerPostulacionesActivity.this, "Error al cargar", Toast.LENGTH_SHORT).show());
    }
}

