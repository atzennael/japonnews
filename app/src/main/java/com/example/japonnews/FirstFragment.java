package com.example.japonnews;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstFragment extends Fragment {
    private ViewPager2 viewPager;
    private Handler handler = new Handler();
    private Runnable runnable;

    private RecyclerView recyclerOfertaLaboral, recyclerPasantia, recyclerProyecto, recyclerPracticaPreprofesional;
    private FirebaseFirestore db;
    private Button btnCargarMas;
    private clasifAdapter ofertaLaboralAdapter, pasantiaAdapter, proyectoAdapter, practicaPreprofesionalAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);
                    guardarToken(token);
                });


        // Configuración del ViewPager
        viewPager = view.findViewById(R.id.viewPager);
        List<String> images = Arrays.asList(
                "https://i.pinimg.com/236x/f4/db/8a/f4db8a52c1b887bad46a301afa6b7167.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTVtzurk5_92_SS30O6Zgq89G06jLY2aC5B2w&s",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQraQ0F9c2NVwpSwoLcrCKvStaX5N_yd8EGXQ&s"
        );
        ImageAdapter adapter = new ImageAdapter(requireContext(), images);
        viewPager.setAdapter(adapter);
        autoScroll(viewPager, images.size());

        db = FirebaseFirestore.getInstance();

        recyclerOfertaLaboral = view.findViewById(R.id.recyclerOfertaLaboral);
        recyclerPasantia = view.findViewById(R.id.recyclerPasantia);
        recyclerProyecto = view.findViewById(R.id.recyclerProyecto);
        recyclerPracticaPreprofesional = view.findViewById(R.id.recyclerPracticaPreprofesional);
        btnCargarMas=view.findViewById(R.id.btnCargasMas);

        ofertaLaboralAdapter = new clasifAdapter(requireContext(), new ArrayList<>());
        pasantiaAdapter = new clasifAdapter(requireContext(), new ArrayList<>());
        proyectoAdapter = new clasifAdapter(requireContext(), new ArrayList<>());
        practicaPreprofesionalAdapter = new clasifAdapter(requireContext(), new ArrayList<>());

        recyclerOfertaLaboral.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recyclerPasantia.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recyclerProyecto.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        recyclerPracticaPreprofesional.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));

        recyclerOfertaLaboral.setAdapter(ofertaLaboralAdapter);
        recyclerPasantia.setAdapter(pasantiaAdapter);
        recyclerProyecto.setAdapter(proyectoAdapter);
        recyclerPracticaPreprofesional.setAdapter(practicaPreprofesionalAdapter);

        cargarClasificados();

        btnCargarMas.setOnClickListener(v -> {
            Fragment secondFragment = new SecondFragment();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, secondFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

    }

    private void cargarClasificados() {
        db.collection("clasificados")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult()!=null) {
                        List<clasif_modelo> ofertaLaboralList = new ArrayList<>();
                        List<clasif_modelo> pasantiaList = new ArrayList<>();
                        List<clasif_modelo> proyectoList = new ArrayList<>();
                        List<clasif_modelo> practicaList = new ArrayList<>();

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            clasif_modelo clasificado = new clasif_modelo(
                                    document.getString("titulo"),
                                    document.getString("detalle"),
                                    document.getString("imagen"),
                                    document.getString("tipoPublicacion")
                            );
                            Log.d("Firebase", "Clasificado recibido: " + clasificado.getTitulo()
                                    + " - " + clasificado.getTipoPublicacion());
                            // Filtramos los clasificados por tipoPublicacion
                            switch (clasificado.getTipoPublicacion()) {
                                case "Oferta Laboral":
                                    ofertaLaboralList.add(clasificado);
                                    break;
                                case "Pasantía":
                                    pasantiaList.add(clasificado);
                                    break;
                                case "Proyecto":
                                    proyectoList.add(clasificado);
                                    break;
                                case "Práctica Preprofesional":
                                    practicaList.add(clasificado);
                                    break;
                                default:
                                    Log.e("Firebase", "Tipo de publicación desconocido: " +
                                            clasificado.getTipoPublicacion());
                            }
                        }

                        // Actualizamos los adaptadores con los clasificados filtrados
                        ofertaLaboralAdapter.updateList(ofertaLaboralList);
                        pasantiaAdapter.updateList(pasantiaList);
                        proyectoAdapter.updateList(proyectoList);
                        practicaPreprofesionalAdapter.updateList(practicaList);
                        Log.d("Firebase", "Datos cargados correctamente: " +
                                "Oferta Laboral: " + ofertaLaboralList.size() + ", " +
                                "Pasantía: " + pasantiaList.size() + ", " +
                                "Proyecto: " + proyectoList.size() + ", " +
                                "Práctica: " + practicaList.size());
                        } else {
                        Log.e("Firebase", "Error al obtener los clasificados", task.getException());
                    }
                });
    }

    private void autoScroll(ViewPager2 viewPager, int size) {
        runnable = new Runnable() {
            int currentPage = 0;

            @Override
            public void run() {
                if (currentPage >= size) currentPage = 0;
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }

    private void guardarToken(String token) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                .update("fcmToken", token)
                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token guardado en Firestore"))
                .addOnFailureListener(e -> Log.e("FCM", "Error al guardar token", e));
    }
}