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
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.japonnews.R
import com.example.japonnews.databinding.ActivityLoginBinding
import com.example.japonnews.home1
import com.example.japonnews.signup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val currentUser: FirebaseUser? = auth.currentUser

        if (currentUser != null) {
            startActivity(Intent(this, home1::class.java))
            finish()
            return
        }
        val outlook = findViewById <ImageView>(R.id.outlook)
        outlook.setOnClickListener {
            signInWithMicrosoft()
        }

        val login = binding.login

        login.setOnClickListener {
            binding.loading.visibility = View.VISIBLE
            checkUserExists(binding.username.text.toString(), binding.password.text.toString())
        }

        val textView = findViewById<TextView>(R.id.textView2)
        textView.setOnClickListener {
            val signupIntent = Intent(this, signup::class.java)
            startActivity(signupIntent)
        }
    }

    private fun checkUserExists(email: String, password: String) {
        db.collection("usuarios").document(email).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    loginUser(email, password)
                } else {
                    binding.loading.visibility = View.GONE
                    Toast.makeText(this, "El usuario no está registrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                binding.loading.visibility = View.GONE
                Toast.makeText(this, "Error al verificar usuario", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                binding.loading.visibility = View.GONE
                if (task.isSuccessful) {
                    val home = Intent(this, home1::class.java)
                    startActivity(home)
                    finish()
                } else {
                    Toast.makeText(this, "Usuario y/o contraseña incorrecto", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun goToHome() {
        val intent = Intent(this, home1::class.java)
        startActivity(intent)
        finish()
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