package com.example.japonnews;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class new_job extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private FirebaseAuth auth;
    private ImageView imageView;

    private EditText editTextTitulo, editTextDetalle;
    private Button buttonCrear, buttonCargar;
    private Uri imageUri;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_job);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        storage = FirebaseStorage.getInstance();

        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextDetalle = findViewById(R.id.editTextDetalle);
        buttonCargar = findViewById(R.id.buttonCargar);
        buttonCrear = findViewById(R.id.buttonCrear);
        imageView = findViewById(R.id.imageView6);

        buttonCargar.setOnClickListener(v -> cargarImagen());
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    private Uri fotoUri;
    private void abrirCamara(){
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null){
            try{
                File fotoFile = crearArchivoImg();
                if (fotoFile !=null){
                    fotoUri = FileProvider.getUriForFile(this,"com.japonnews.android.fileprovider",
                            fotoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fotoUri);
                    startActivityForResult(intent, CAMERA_REQUEST);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al abrir la cámara", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File crearArchivoImg() throws IOException{
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imgName = "IMG_" + timestamp + "_";
        File storageAddy = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imgName, ".jpg",storageAddy);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==PICK_IMAGE_REQUEST&&data!=null&&data.getData()!=null){
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
            } else if (requestCode==CAMERA_REQUEST){
                if (fotoUri !=null){
                    imageUri = fotoUri;
                    imageView.setImageURI(imageUri);
                } else {
                    Toast.makeText(this, "Error al obtener la imagen de la cámara", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private Uri getImgBitmap(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "IMG_"
                +System.currentTimeMillis(),null);
        return Uri.parse(path);
    }

    private void subirImagen(){
        if(imageUri!=null){
            String userId = auth.getCurrentUser().getUid();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference fileReference = storageReference.child("clasificados/"+userId+"/"+System.currentTimeMillis()+".jpg");
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        guardarImagen(uri.toString());
                        Toast.makeText(new_job.this, "Tu imagen se cargó correctamente", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(new_job.this, "No se pudo cargar la imagen.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    });

        }
    }

    private void guardarImagen(String ImgUrl){
        String userId= auth.getCurrentUser().getUid();
        Map<String, Object> data = new HashMap<>();
        data.put("ImgUrl", ImgUrl);
        data.put("userId", userId);
        data.put("timestamp", FieldValue.serverTimestamp());

        db.collection("clasificados").add(data)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Imagen guardada", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Imagen no guardada", Toast.LENGTH_SHORT).show());
    }

    private void cargarImagen(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona una opción").setItems(new String[]{"Galería", "Cámara"},
                (dialog, which) ->{
                    if (which==0){
                        abrirGaleria();
                    } else {
                        abrirCamara();
                    }
                }).show();
    }
}