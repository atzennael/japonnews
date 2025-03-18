package com.example.japonnews.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.japonnews.R
import com.example.japonnews.databinding.ActivityLoginBinding
import com.example.japonnews.home1
import com.example.japonnews.signup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            goToHome()
            return
        }
        val outlook = findViewById<ImageView>(R.id.outlook)
        outlook.setOnClickListener {
            signInWithMicrosoft()
        }

        binding.login.setOnClickListener {
            val email = binding.username.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(
                    this,
                    "Por favor, ingresa tu correo y contraseña",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            binding.loading.visibility = android.view.View.VISIBLE
            loginUser(email, password)
        }

        val textView = findViewById<TextView>(R.id.textView2)
        textView.setOnClickListener {
            val signupIntent = Intent(this, signup::class.java)
            startActivity(signupIntent)
        }

        binding.password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.login.performClick()
                return@setOnEditorActionListener true
            }
            false
        }
    }
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.loading.visibility = View.GONE
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "Inicio de sesión exitoso")
                    Toast.makeText(this, "Bienvenido a Japon News", Toast.LENGTH_SHORT).show()
                    goToHome()
                } else {
                    val errorMsg = task.exception?.localizedMessage ?: "Error desconocido"
                    Log.e("LoginActivity", "Error en inicio de sesión: $errorMsg")
                    Toast.makeText(this, "Error: $errorMsg", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun goToHome() {
        val intent = Intent(this, home1::class.java)
        startActivity(intent)
        finish()
    }

    private fun signInWithMicrosoft() {
        val provider = OAuthProvider.newBuilder("microsoft.com")
        provider.addCustomParameter("prompt", "select_account")

        val auth = FirebaseAuth.getInstance()
        val pendingResultTask = auth.pendingAuthResult

        if (pendingResultTask != null) {
            pendingResultTask
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        verificarRegistroUsuario(user)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AuthError", "Error: ${it.message}")
                }
        } else {
            auth.startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        verificarRegistroUsuario(user)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AuthError", "Error: ${it.message}")
                }
        }
    }

    private fun verificarRegistroUsuario(user: FirebaseUser) {
        val email = user.email
        val db = FirebaseFirestore.getInstance()

        if (email != null) {
            db.collection("usuarios").whereEqualTo("email", email).get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        // El usuario ya está registrado
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        goToHome()
                    } else {
                        // El usuario no está registrado
                        Toast.makeText(this, "Usuario no registrado. Completa tu perfil", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, signup::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al verificar usuario", Toast.LENGTH_SHORT).show()
                    Log.e("FirestoreError", "Error al buscar usuario en Firestore: ${it.message}")
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el email del usuario", Toast.LENGTH_SHORT).show()
        }
    }

}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}