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

class LoginActivity : AppCompatActivity() {

    // private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    //private lateinit var db: FirebaseFirestore

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

        val pendingResultTask = FirebaseAuth.getInstance().pendingAuthResult
        if (pendingResultTask != null) {
            // Si ya hay una sesión pendiente, intenta iniciar sesión automáticamente
            pendingResultTask
                .addOnSuccessListener {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    goToHome()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AuthError", "Error: ${it.message}")
                    Log.e("AuthError", "Error: $it")

                }
        } else {
            // Si no hay sesión pendiente, iniciar sesión manualmente
            FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider(this, provider.build())
                .addOnSuccessListener {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    goToHome()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AuthError", "Error: ${it.message}")
                    Log.e("AuthError", "Error: $it")
                }
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