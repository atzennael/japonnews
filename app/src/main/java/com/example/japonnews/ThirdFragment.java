package com.example.japonnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ThirdFragment extends Fragment {
    private RecyclerView recyclerView;
    private clasifAdapter adapter;
    private List <clasif_modelo> listaSaved = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_third, container, false);
        recyclerView=view.findViewById(R.id.recyclerViewSaved);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarSavedC();
        adapter = new clasifAdapter(getContext(), listaSaved);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void cargarSavedC(){
        SharedPreferences preferences = requireActivity().getSharedPreferences("Guardados", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String savedJson = preferences.getString("guardados", null);

        if (savedJson!=null){
            Type type=new TypeToken<List<clasif_modelo>>() {}.getType();
            listaSaved.clear();
            listaSaved.addAll(gson.fromJson(savedJson, type));

            for (clasif_modelo item:listaSaved) {
                Log.d("ThirdFragment", "Elemento cargado - Título: " + item.getTitulo()
                        + ", Detalle: " + item.getDetalle() + ", Imagen: " + item.getImagen());
            }
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            } } else {
            Log.d("ThirdFragment", "no hay datos");
        }
    }
}