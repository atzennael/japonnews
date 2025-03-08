package com.example.japonnews;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class clasifAdapter extends RecyclerView.Adapter<clasifAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<clasif_modelo> clasificados;
    private List<clasif_modelo> clasF;

    public clasifAdapter(Context context, List<clasif_modelo> clasificados) {
        this.context = context;
        this.clasificados = clasificados;
        this.clasF = new ArrayList<>(clasificados);
    }

    public void updateList(List<clasif_modelo> newList) {
        Log.d("Adapter", "Actualizando lista con " + newList.size() + " elementos");
        clasF.clear();
        clasF.addAll(newList);
        clasificados.clear();
        clasificados.addAll(newList);
        clasF = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_clasif, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            clasif_modelo clasificado = clasificados.get(position);
        holder.bind(clasificado, context);

        holder.titulo.setText(clasificado.getTitulo());
        holder.detalle.setText(clasificado.getDetalle());

        if (clasificado.getImagen() != null && !clasificado.getImagen().isEmpty()) {
            Picasso.get().load(clasificado.getImagen()).into(holder.imagen);
        } else{
            holder.imagen.setImageResource(R.drawable.japonnews_logo);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, clasificado.class);
            intent.putExtra("titulo", clasificado.getTitulo());
            intent.putExtra("detalle", clasificado.getDetalle());
            intent.putExtra("imagen", clasificado.getImagen());
            intent.putExtra("tipoPublicacion", clasificado.getTipoPublicacion());

            Log.d("SavedActivity", "Enviando a clasificado: " + clasificado.getTitulo() + ", " + clasificado.getDetalle() + ", " + clasificado.getImagen());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return clasificados.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, detalle;
        ImageView imagen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tvTitulo);
            detalle = itemView.findViewById(R.id.tvDetalle);
            imagen = itemView.findViewById(R.id.ivImagen);
        }

        public void bind(clasif_modelo clasificado, Context context) {
            titulo.setText(clasificado.getTitulo());
            detalle.setText(clasificado.getDetalle());

            if (clasificado.getImagen() != null && !clasificado.getImagen().isEmpty()) {
                Picasso.get().load(clasificado.getImagen()).into(imagen);
            }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, clasificado.class);
                intent.putExtra("titulo", clasificado.getTitulo());
                intent.putExtra("detalle", clasificado.getDetalle());
                intent.putExtra("imagen", clasificado.getImagen());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public Filter getFilter() {
        return Filtro;
    }
    private final Filter Filtro = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<clasif_modelo> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(clasF);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (clasif_modelo item : clasF) {
                    if (item.getTitulo().toLowerCase().contains(filterPattern)
                            || item.getDetalle().toLowerCase().contains(filterPattern)
                            || item.getTipoPublicacion().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            if(filteredList.isEmpty()){
                Log.d("Filtro", "No se encontraron coincidencias");
            } else {
                Log.d("Filtro", "Elementos filtrados: " + filteredList.size());
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clasificados.clear();
            if(results.values!=null){
                clasificados.addAll((List<clasif_modelo>) results.values);
            }
            notifyDataSetChanged();
        }
    };
}
