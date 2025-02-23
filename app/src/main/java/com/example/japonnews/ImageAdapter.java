package com.example.japonnews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.japonnews.R;
import com.squareup.picasso.Picasso;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> images;
    private Context context;

    public ImageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carousel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imageUrl = images.get(position);

        if (imageUrl.startsWith("http")) {
            // Si es una URL de internet, usar Picasso
            Picasso.get().load(imageUrl).into(holder.imageView);
            //holder.textViewSubtitle.setText(images.getSubtitle());
        } else {
            // Si es un recurso local, usar setImageResource()
            try {
                int imageResId = Integer.parseInt(imageUrl);
                holder.imageView.setImageResource(imageResId);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textViewSubtitle = itemView.findViewById(R.id.textViewSubtitle);
        }
    }
}