package com.example.japonnews;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class home1 extends AppCompatActivity {

    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    FourthFragment fourthFragment = new FourthFragment();
    FifthFragment fifthFragment= new FifthFragment();

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FirstFragment())
                    .commit();
        }

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        obtenerYGuardarTokenFCM();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.nav_home){
                loadFragment(firstFragment);
                return true;
            } else if (id == R.id.nav_clasif) {
                loadFragment(secondFragment);
                return true;
            } else if (id == R.id.nav_saved) {
                loadFragment(thirdFragment);
                return true;
            } else if (id == R.id.nav_profile) {
                loadFragment(fourthFragment);
                return true;
            } else if (id== R.id.nav_notification){
                loadFragment(fifthFragment);
                return true;
            }

            return false;
        }
    };

    private void obtenerYGuardarTokenFCM() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FirstFragment", "Error obteniendo el token FCM", task.getException());
                        return;
                    }

                    // Obtén el token
                    String token = task.getResult();
                    Log.d("FirstFragment", "Token FCM: " + token);

                    // Guarda el token en Firestore
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    if (auth.getCurrentUser() != null) {
                        String userId = auth.getCurrentUser().getUid();
                        db.collection("usuarios").document(userId)
                                .update("tokenFCM", token)
                                .addOnSuccessListener(aVoid -> Log.d("FirstFragment", "Token FCM guardado en Firestore"))
                                .addOnFailureListener(e -> Log.e("FirstFragment", "Error al guardar el token FCM", e));
                    } else {
                        Log.e("FirstFragment", "Usuario no autenticado, no se puede guardar el token FCM");
                    }
                });
    }

    public void loadFragment (Fragment fragment){
        FragmentTransaction  transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

}