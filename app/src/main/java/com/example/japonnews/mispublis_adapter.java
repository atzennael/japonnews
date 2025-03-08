package com.example.japonnews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class mispublis_adapter extends RecyclerView.Adapter<mispublis_adapter.ViewHolder> {

    private List<publis_modelo> listaP;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Context context;

    public mispublis_adapter(Context context, List<publis_modelo> listaP) {
        this.listaP = listaP;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mis_publicaciones, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        publis_modelo publicacion = listaP.get(position);
        holder.txtTitulo.setText(publicacion.getTitulo());
        holder.txtDetalle.setText(publicacion.getDetalle());

        if (publicacion.getImagen() != null && !publicacion.getImagen().isEmpty()) {
            Picasso.get().load(publicacion.getImagen()).into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.japonnews_logo);
        }

        FirebaseUser user = auth.getCurrentUser();
        if (user != null && publicacion.getUserId() != null && publicacion.getUserId().equals(user.getUid())) {
            holder.btnEliminar.setVisibility(View.VISIBLE);
        } else {
            holder.btnEliminar.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, clasificado.class);
            intent.putExtra("titulo", publicacion.getTitulo());
            intent.putExtra("detalle", publicacion.getDetalle());
            intent.putExtra("imagen", publicacion.getImagen());
            intent.putExtra("tipoPublicacion", publicacion.getTipoPublicacion());

            Log.d("SavedActivity", "Enviando a clasificado: " + publicacion.getTitulo() + ", " + publicacion.getDetalle() + ", " + publicacion.getImagen());
            context.startActivity(intent);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Confirmar eliminación");
            builder.setMessage("¿Estás seguro de que quieres eliminar esta publicación?");
            Log.d("MisPublis", "Se presionó btnEliminar");

            builder.setPositiveButton("Sí", (dialog, which) -> {
                eliminarPublicacion(publicacion.getClasifId());
                Log.d("MisPublis", "Se llamó eliminarPublicación");
            });

            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return listaP.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDetalle;
        Button btnEliminar;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitulo = itemView.findViewById(R.id.tvTitulo);
            txtDetalle = itemView.findViewById(R.id.tvDetalle);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            imagen = itemView.findViewById(R.id.ivImagen);
        }
    }

    private void eliminarPublicacion(String idPublicacion) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("clasificados").document(idPublicacion)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Error eliminando publicación", e);
                });
    }
}

