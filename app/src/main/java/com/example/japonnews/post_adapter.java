package com.example.japonnews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class post_adapter extends RecyclerView.Adapter<post_adapter.ViewHolder> {
    private List<post_modelo> listaPostulaciones;

    public post_adapter(List<post_modelo> listaPostulaciones) {
        this.listaPostulaciones = listaPostulaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        post_modelo postulacion = listaPostulaciones.get(position);
        holder.textViewNombre.setText(postulacion.getNombre());
        holder.textViewCorreo.setText(postulacion.getCorreo());
        holder.textViewTelefono.setText(postulacion.getTelefono());

    }

    @Override
    public int getItemCount() {
        return listaPostulaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewNombre, textViewCorreo, textViewTelefono;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.textViewNombre);
            textViewCorreo = itemView.findViewById(R.id.textViewCorreo);
            textViewTelefono = itemView.findViewById(R.id.textViewTelefono);

        }
    }
}

