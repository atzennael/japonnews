package com.example.japonnews;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class new_job extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ImageView imageView;
    private EditText editTextTitulo, editTextDetalle;
    private Button buttonCrear, buttonCargar;
    private Uri imageUri;
    private Spinner spinner;
    private Timestamp fecha;
    private String tipoPublicacion;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job);

        db= FirebaseFirestore.getInstance();
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();

        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDetalle = findViewById(R.id.editTextDetalle);
        buttonCargar = findViewById(R.id.buttonCargar);
        buttonCrear = findViewById(R.id.buttonCrear);
        imageView = findViewById(R.id.imageView6);
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.tiposSpinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        buttonCargar.setOnClickListener(v -> abrirGaleria());
        buttonCrear.setOnClickListener(v-> imgClasif());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }
    private void imgClasif(){
        String titulo= editTextTitulo.getText().toString().trim();
        String detalle = editTextDetalle.getText().toString().trim();
        fecha = new Timestamp(new Date());
        tipoPublicacion=spinner.getSelectedItem().toString();

        if (titulo.isEmpty()||detalle.isEmpty()){
            Toast.makeText(this,"Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }
        if(imageUri==null){
            Toast.makeText(this,"Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId=auth.getCurrentUser().getUid();
        String clasifId=db.collection("clasificados").document().getId();
        StorageReference storageReference = storage.getReference().child("clasificados/"+userId+
                "/"+clasifId+".jpg");
        storageReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            guardarClasif(clasifId, userId, titulo, detalle, uri.toString(), tipoPublicacion, fecha);
                        }))
                .addOnFailureListener(e -> {
                    Log.e("Error clasif", e.getMessage());
                    Toast.makeText(this, "Error al crear el clasificado",Toast.LENGTH_SHORT).show();
                });
    }
    private void guardarClasif(String clasifId, String userId, String titulo, String detalle, String imageUri,
                               String tipoPublicacion, Timestamp fecha){
        Map<String, Object> clasificado = new HashMap<>();
        clasificado.put("clasifId", clasifId);
        clasificado.put("userId", userId);
        clasificado.put("titulo", titulo);
        clasificado.put("detalle", detalle);
        clasificado.put("imagen", imageUri);
        clasificado.put("tipoPublicacion", tipoPublicacion);
        clasificado.put("fecha", fecha);

        db.collection("clasificados").document(clasifId)
                .set(clasificado)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Tu publicación fue creada con éxito", Toast.LENGTH_SHORT).show();

                    Log.d("Clasificado", "Título: " + titulo);
                    Log.d("Clasificado", "Detalle: " + detalle);
                    Log.d("Clasificado", "Imagen URL: " + imageUri);

                    Intent intent = new Intent(this, clasificado.class);
                    intent.putExtra("titulo", titulo);
                    intent.putExtra("detalle", detalle);
                    intent.putExtra("imagen", imageUri);
                    intent.putExtra("tipoPublicacion", tipoPublicacion);
                    intent.putExtra("fecha", fecha);
                    startActivity(intent);
                    guardarMisPublicaciones();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("Error clasif", e.getMessage());
                    Toast.makeText(this, "No se pudo crear tu publicación. Intenta nuevamente", Toast.LENGTH_SHORT).show();
                });
    }

    private void guardarMisPublicaciones(){

    }
}