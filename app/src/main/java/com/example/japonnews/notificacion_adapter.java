package com.example.japonnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class notificacion_adapter extends RecyclerView.Adapter<notificacion_adapter.ViewHolder> {

    private Context context;
    private List<notificacion_modelo> listaNotificaciones;
    private OnItemClickListener listener;

    public notificacion_adapter(Context context, List<notificacion_modelo> listaNotificaciones, OnItemClickListener listener) {
        this.context = context;
        this.listaNotificaciones = listaNotificaciones;
        this.listener = listener;
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
        if (notificacion != null) {
            holder.tvNotificacion.setText("Alguien se ha postulado a tu publicación");
            holder.tvDetalleNotificacion.setText(notificacion.getMensaje());
        }
        holder.itemView.setOnClickListener(v -> listener.onItemClick(notificacion));
    }
    public void actualizarLista(List<notificacion_modelo> nuevasNotificaciones) {
        listaNotificaciones.clear();
        listaNotificaciones.addAll(nuevasNotificaciones);
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNotificacion, tvDetalleNotificacion;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNotificacion=itemView.findViewById(R.id.tvNotificacion);
            tvDetalleNotificacion=itemView.findViewById(R.id.tvDetalleNotificacion);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(notificacion_modelo notificacion);
    }
}

