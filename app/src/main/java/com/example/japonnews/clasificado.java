package com.example.japonnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

public class clasificado extends AppCompatActivity {

    private TextView textViewTitulo, textViewDetalle;
    private ImageView imageViewClasificado;
    private Button btnGuardar, btnPostularme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clasificado);

        textViewTitulo = findViewById(R.id.textViewTitulo);
        textViewDetalle = findViewById(R.id.textViewDetalle);
        imageViewClasificado = findViewById(R.id.imageView5);
        btnGuardar = findViewById(R.id.button5);
        btnPostularme = findViewById(R.id.button6);

        // Recibir datos del intent
        Intent intent = getIntent();
        if (intent != null) {
            String titulo = intent.getStringExtra("titulo");
            String detalle = intent.getStringExtra("detalle");
            String imageUrl = intent.getStringExtra("imagen");

            Log.d("ClasificadoActivity", "Título recibido: " + titulo);
            Log.d("ClasificadoActivity", "Detalle recibido: " + detalle);

            textViewTitulo.setText(titulo);
            textViewDetalle.setText(detalle);

            // Cargar imagen desde URL con Glide
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.japonnews_logo) // Imagen temporal mientras carga
                    .error(R.drawable.error) // Imagen si falla la carga
                    .into(imageViewClasificado);
        } else {
            Log.e("ClasificadoActivity", "No se recibieron datos en el Intent");
        }
    }
}