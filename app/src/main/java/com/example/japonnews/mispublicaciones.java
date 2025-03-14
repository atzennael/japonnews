package com.example.japonnews;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class mispublicaciones extends AppCompatActivity {
    private RecyclerView recyclerView;
    private mispublis_adapter adapter;
    private List<publis_modelo> listaP;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_publicaciones);

        recyclerView = findViewById(R.id.recyclerViewMisPublicaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listaP = new ArrayList<>();
        adapter = new mispublis_adapter(this, listaP);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        cargarMisPublicaciones();
    }

    private void cargarMisPublicaciones() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        db.collection("clasificados")
                .whereEqualTo("userId", user.getUid())
                .orderBy("fecha")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<publis_modelo> lista = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        publis_modelo p = doc.toObject(publis_modelo.class);
                        if (p != null) {
                            p.setClasifId(doc.getId());  // Guardar el ID de Firestore
                            lista.add(p);
                            Log.d("Firestore", "Publicación cargada: " + p.getTitulo());
                        } else {
                            Log.e("Firestore", "Documento con datos nulos: " + doc.getId());
                        }
                    }
                    adapter.notifyDataSetChanged();


                    try {
                        recyclerView.setAdapter(new mispublis_adapter(getApplicationContext(), lista));
                    } catch (Exception e) {
                        Log.e("RecyclerView", "Error al cargar adaptador", e);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar publicaciones", e));
    }
}