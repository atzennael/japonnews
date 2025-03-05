package com.example.japonnews;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FifthFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userId;
    private RecyclerView recyclerView;
    private notificacion_adapter adapter;
    private List<notificacion_modelo> listaNotificaciones;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fifth, container, false);

         auth = FirebaseAuth.getInstance();
         db = FirebaseFirestore.getInstance();
         userId = auth.getCurrentUser().getUid();

         recyclerView=view.findViewById(R.id.recyclerViewNotificaciones);
         recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

         listaNotificaciones=new ArrayList<>();
         adapter = new notificacion_adapter(listaNotificaciones);
         recyclerView.setAdapter(adapter);

         cargarNotificaciones();

         return view;

         private void cargarNotificaciones(){
            db.collection("notificaciones")
                    .whereEqualTo("id_receptor", userId)
                    .orderBy("fecha", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        listaNotificaciones.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            notificacion_modelo notificacion = doc.toObject(notificacion_modelo.class);
                            listaNotificaciones.add(notificacion);
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar notificaciones", e));
        }


    }
}
