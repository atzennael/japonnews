package com.example.japonnews;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class notificacion_adapter extends RecyclerView.Adapter<notificacion_adapter.ViewHolder> {

    private List<notificacion_modelo> listaNotificaciones;
    private TextView tvNotificacion, tvFecha;

    public notificacion_adapter(List<notificacion_modelo> listaNotificaciones) {
        this.listaNotificaciones = listaNotificaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        notificacion_modelo notificacion = listaNotificaciones.get(position);
        holder.tvNotificacion.setText(notificacion.getMensaje());
        if (notificacion.getFecha() != null) {
            holder.tvFecha.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm",
                    Locale.getDefault()).format(notificacion.getFecha().toDate()));
        }
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotificacion, tvFecha;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNotificacion = itemView.findViewById(R.id.tvNotificacion);
            tvFecha = itemView.findViewById(R.id.tvDetalleNotificacion);
        }
    }
}

