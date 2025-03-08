package com.example.japonnews;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import com.example.japonnews.ui.login.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private clasifAdapter adapter;
    private FirebaseFirestore db;
    private SearchView searchView;
    private clasificadoVM clasificadosViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity();
        } else {
            FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), new_job.class);
                    startActivity(intent);
                }
            });
        }
        recyclerView = view.findViewById(R.id.recyclerViewClasificados);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchView=view.findViewById(R.id.searchview);
        clasificadosViewModel = new ViewModelProvider(this).get(clasificadoVM.class);

        adapter = new clasifAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        clasificadosViewModel.getClasificados().observe(getViewLifecycleOwner(), clasificados -> {
            if (clasificados != null) {
                adapter.updateList(clasificados);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return view;
    }

    public static class clasificadoVM extends ViewModel {
        private MutableLiveData<List<clasif_modelo>> clasificadosLiveData = new MutableLiveData<>();
        private FirebaseFirestore db = FirebaseFirestore.getInstance();

        public clasificadoVM() {
            cargarData();
        }

        public LiveData<List<clasif_modelo>> getClasificados() {
            return clasificadosLiveData;
        }

        private void cargarData() {
            db.collection("clasificados")
                    .orderBy("fecha")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<clasif_modelo> clasificados = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                clasificados.add(new clasif_modelo(
                                        document.getString("titulo"),
                                        document.getString("detalle"),
                                        document.getString("imagen"),
                                        document.getString("tipoPublicacion")
                                ));
                            }
                            clasificadosLiveData.setValue(clasificados);
                        } else {
                            Log.e("Firebase", "Error al obtener documentos", task.getException());
                        }
                    });
        }
    }
}