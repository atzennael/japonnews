package com.example.japonnews

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.japonnews.ui.login.LoginActivity
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)

        lateinit var snsClient: AmazonSNS

            // Configurar credenciales de AWS
            val credentialsProvider = CognitoCachingCredentialsProvider(
                applicationContext,
                "us-east-2:be3fdf56-a542-4024-ba82-a04cad8f900d", // Reemplaza con tu ID de usuario de Cognito
                Regions.US_EAST_2 // Cambia por la región de tu SNS
            )

            snsClient = AmazonSNSClient(credentialsProvider)

        // Mostrar la pantalla de carga por 3 segundos y luego ir a la pantalla de inicio de sesión
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}