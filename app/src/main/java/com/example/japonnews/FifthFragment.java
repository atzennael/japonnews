package com.example.japonnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FifthFragment extends Fragment implements notificacion_adapter.OnItemClickListener {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userId;
    private RecyclerView recyclerView;
    private notificacion_adapter adapter;
    private List<notificacion_modelo> listaNotificaciones;

    private TextView tvMensaje;
    private String mensaje, id_usuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fifth, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        listaNotificaciones = new ArrayList<>();
        adapter = new notificacion_adapter(requireContext(), listaNotificaciones, this);
        recyclerView.setAdapter(adapter);

        cargarNotificaciones();

        return view;
    }

    @Override
    public void onItemClick(notificacion_modelo notificacion) {
        Log.d("Notificacion", "Se seleccionó la notificación: " + notificacion.getTitulo());
        Log.d("Notificacion", "ID Usuario: " + notificacion.getId_usuario());
        Log.d("Notificacion", "Mensaje: " + notificacion.getMensaje());

        // Abrir la actividad post_info con los datos de la notificación
        Intent intent = new Intent(getActivity(), post_info.class);
        intent.putExtra("id_postulante", notificacion.getId_usuario());
        intent.putExtra("mensaje", notificacion.getMensaje());
        startActivity(intent);
    }

    private void cargarNotificaciones() {
        db.collection("clasificados")
                .whereEqualTo("userId", userId) // Buscar las publicaciones creadas por este usuario
                .get()
                .addOnSuccessListener(clasificados -> {
                    List<String> ClasifIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : clasificados) {
                        ClasifIds.add(doc.getId()); // Guarda los IDs de las publicaciones del usuario
                    }

                    if (ClasifIds.isEmpty()) return; // Si el usuario no tiene publicaciones, no hay notificaciones

                    // Ahora buscar postulaciones que correspondan a esas publicaciones
                    db.collection("postulaciones")
                            .whereIn("id_publicacion", ClasifIds)
                            //.orderBy("fecha_postulacion", Query.Direction.DESCENDING)// Buscar postulaciones recibidas en esas publicaciones
                            .get()
                            .addOnSuccessListener(postulaciones -> {
                                listaNotificaciones.clear();
                                for (QueryDocumentSnapshot doc : postulaciones) {
                                    notificacion_modelo notificacion = doc.toObject(notificacion_modelo.class);
                                    listaNotificaciones.add(notificacion);
                                }
                                adapter.notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error cargando postulaciones", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error cargando publicaciones del usuario", e));
    }
}