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

public class verPostulacion extends AppCompatActivity {
    private RecyclerView recyclerView;
    private post_adapter adapter;
    private List<post_modelo> listaPostulaciones = new ArrayList<>();
    private FirebaseFirestore db;
    private String ofertaID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_post);

        recyclerView = findViewById(R.id.recyclerViewPostulaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new post_adapter(listaPostulaciones);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

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
                .addOnFailureListener(e -> Toast.makeText(verPostulacion.this, "Error al cargar", Toast.LENGTH_SHORT).show());
    }
}

