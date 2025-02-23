package com.example.japonnews;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.japonnews.conexion.MySQLConexion;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

import com.example.japonnews.ui.login.LoginActivity;

public class signup extends AppCompatActivity {

    private EditText editTextNombre;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth auth;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        editTextNombre = findViewById(R.id.editTextText);
        EditText editTextIdentificacion = findViewById(R.id.editTextNumber);
        editTextEmail = findViewById(R.id.editTextTextEmailAddress);
        editTextPassword = findViewById(R.id.editTextPassword);
        Button btnCrearCuenta = findViewById(R.id.login3);

        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("SignupActivity", "Botón Crear Cuenta presionado");
                Toast.makeText(signup.this, "Botón presionado", Toast.LENGTH_SHORT).show();
                registrarUsuario();
            }
        });

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

        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Ingresa un correo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (auth == null) {
            Log.e("SignupActivity", "FirebaseAuth es null. Revisa la inicialización.");
        } else {
            Log.d("SignupActivity", "FirebaseAuth inicializado correctamente.");
        }
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String firebaseUID = user.getUid();
                            MySQLConexion.registrarUsuario(this, firebaseUID, nombre, email, null);
                            Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(signup.this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Error desconocido";
                        Log.e("SignupActivity", "Error en el registro: " + errorMsg);                    }
                });
    }
}