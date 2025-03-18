package com.example.japonnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import androidx.appcompat.app.AppCompatActivity;
import com.example.japonnews.ui.login.LoginActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextIdentificacion;
    private EditText editTextNumero;
    private FirebaseAuth auth;
    private ImageView imgProfile;
    private Uri imgUri;
    private Button btnCargarImg;
    private Button btnCrearCuenta;
    private static final int PICK_IMAGE_REQUEST =1;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        editTextNombre = findViewById(R.id.editTextText);
        editTextIdentificacion = findViewById(R.id.editTextNumber);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextContrasena);
        editTextNumero=findViewById(R.id.editTextNumero);
        btnCrearCuenta = findViewById(R.id.login3);
        imgProfile = findViewById(R.id.imageView2);
        btnCargarImg = findViewById(R.id.cargarImg);

        btnCargarImg.setOnClickListener(v -> {
            Log.d("SignupActivity", "Botón de cargar imagen presionado");
            cargarImagen();
        });
        btnCrearCuenta.setOnClickListener(v -> registrarUsuario());

        TextView textview = findViewById(R.id.textView7);
        textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(signup.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void registrarUsuario() {
        Log.d("SignupActivity", "registrarUsuario() llamado");

        String nombre = editTextNombre.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String id = editTextIdentificacion.getText().toString().trim();
        String telefono = editTextNumero.getText().toString().trim();

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || id.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!email.endsWith("@itsjapon.edu.ec")){
            Toast.makeText(this, "Solo puedes registrarte con correo institucional", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            Map<String, Object> userData = new HashMap<>();
                            userData.put("fcmToken", null);
                            userData.put("uid", userId);
                            userData.put("nombre", nombre);
                            userData.put("id", id);
                            userData.put("telefono", telefono);
                            userData.put("email", email);
                            userData.put("password", password);
                            userData.put("fotoPerfil", null);

                            FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("SignupActivity", "Datos de usuario guardados en Firestore");
                                        guardarImg(userId);
                                        Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(signup.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("SignupActivity", "Error al guardar usuario en Firestore: ", e);
                                    });
                        }
                    } else {
                        if (task.getException() != null) {
                            Log.e("SignupActivity", "Error en el registro: " + task.getException().getMessage());
                        } else {
                            Log.e("SignupActivity", "Error desconocido en el registro");
                        }
                        Toast.makeText(signup.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void cargarImagen(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST&&resultCode==RESULT_OK&&data != null&&data.getData()!=null){
            imgUri = data.getData();
            imgProfile.setImageURI(imgUri);
            Log.d("SignupActivity", "Imagen seleccionada: " + imgUri.toString());
        } else{
            Log.e("SignupActivity", "No se seleccionó ninguna imagen o hubo un error");
        }
    }

    private void guardarImg(String userId){
        if (imgUri!=null){
            StorageReference fileRef = FirebaseStorage.getInstance().getReference("profile_pics")
                    .child(userId+".jpg");
            fileRef.putFile(imgUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                                .update("fotoPerfil", downloadUrl)
                                .addOnSuccessListener(aVoid -> Log.d("SignupActivity", "Foto de perfil guardada"))
                                .addOnFailureListener(e -> Log.e("SignupActivity","Error: ", e));
                    }))
                    .addOnFailureListener(e-> Log.e("SignupActivity", "Error al subir la imagen: ", e));
        }
    }
}