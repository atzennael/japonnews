package com.example.japonnews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class mispublis_adapter extends RecyclerView.Adapter<mispublis_adapter.ViewHolder> {

    private List<Publicacion> listaPublicaciones;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Context context;

    public MisPublicacionesAdapter(List<Publicacion> listaPublicaciones, Context context) {
        this.listaPublicaciones = listaPublicaciones;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clasif, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Publicacion publicacion = listaPublicaciones.get(position);
        holder.txtTitulo.setText(publicacion.getTitulo());
        holder.txtDetalle.setText(publicacion.getDetalle());

        // 🔹 Mostrar el botón SOLO si la publicación pertenece al usuario autenticado
        FirebaseUser user = auth.getCurrentUser();
        if (user != null && publicacion.getUserId().equals(user.getUid())) {
            holder.btnEliminar.setVisibility(View.VISIBLE);
        } else {
            holder.btnEliminar.setVisibility(View.GONE);
        }

        // 🔹 Evento para eliminar la publicación
        holder.btnEliminar.setOnClickListener(v -> eliminarPublicacion(publicacion.getClasifId(), position));
    }

    @Override
    public int getItemCount() {
        return listaPublicaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDetalle;
        Button btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.txtTitulo);
            txtDetalle = itemView.findViewById(R.id.txtDetalle);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }

    // 🔹 Método para eliminar una publicación
    private void eliminarPublicacion(String idPublicacion, int position) {
        db.collection("publicaciones").document(idPublicacion)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                    listaPublicaciones.remove(position);
                    notifyItemRemoved(position);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error al eliminar publicación", e);
                });
    }
}

