package com.example.japonnews;

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

public class home1 extends AppCompatActivity
{
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

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }
                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);
                    guardarToken(token);
                });
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
            }

            return false;
        }
    };

    private void guardarToken(String token) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore.getInstance().collection("usuarios").document(userId)
                .update("fcmToken", token)
                .addOnSuccessListener(aVoid -> Log.d("FCM", "Token guardado en Firestore"))
                .addOnFailureListener(e -> Log.e("FCM", "Error al guardar token", e));
    }

    public void loadFragment (Fragment fragment){
        FragmentTransaction  transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();

    }

}
