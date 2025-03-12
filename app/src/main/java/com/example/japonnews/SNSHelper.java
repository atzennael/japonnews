package com.example.japonnews;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.amazonaws.regions.Region;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;


public class SNSHelper {
    private AmazonSNSClient snsClient;
    private static final String TAG = "SNSHelper";

    public SNSHelper(String identityPoolId, Regions region) {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                MyApplication.getAppContext(),
                "us-east-2:be3fdf56-a542-4024-ba82-a04cad8f900d",
                region
        );
        Map<String, String> logins = new HashMap<>();
        credentialsProvider.setLogins(logins);
        credentialsProvider.refresh();

        snsClient = new AmazonSNSClient(credentialsProvider);
        snsClient.setRegion(Region.getRegion(region));
    }

    public void registerDeviceWithSNS(final String platformApplicationArn) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Log.e(TAG, "El usuario no está autenticado, no se puede registrar en SNS.");
            return;
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);

                    if (token == null || token.isEmpty()) {
                        Log.e(TAG, "FCM Token es nulo o vacío");
                        return;
                    }

                    // Crear un hilo para la ejecución en segundo plano
                    new Thread(() -> {
                        try {
                            // Crear la solicitud para SNS
                            CreatePlatformEndpointRequest request = new CreatePlatformEndpointRequest()
                                    .withPlatformApplicationArn(platformApplicationArn) // Pasar el ARN recibido
                                    .withToken(token);

                            // Crear el endpoint en SNS
                            CreatePlatformEndpointResult result = snsClient.createPlatformEndpoint(request);
                            String endpointArn = result.getEndpointArn();
                            Log.d(TAG, "SNS Endpoint ARN: " + endpointArn);

                            // Guardar en Firestore en el hilo principal
                            new Handler(Looper.getMainLooper()).post(() -> {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                db.collection("usuarios").document(userId)
                                        .update("snsEndpointArn", endpointArn)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Endpoint ARN guardado en Firestore"))
                                        .addOnFailureListener(e -> Log.e(TAG, "Error guardando endpoint ARN en Firestore", e));
                            });

                        } catch (Exception e) {
                            Log.e(TAG, "Error registrando dispositivo con SNS", e);
                        }
                    }).start();
                });
    }

    public void sendNotification(String endpointArn, String titulo, String mensaje) {
        new Thread(() -> {
            try {
                PublishRequest publishRequest = new PublishRequest()
                        .withTargetArn(endpointArn)
                        .withMessage(mensaje)
                        .withSubject(titulo);

                PublishResult result = snsClient.publish(publishRequest);
                Log.d(TAG, "Notificación enviada con éxito. Message ID: " + result.getMessageId());
            } catch (Exception e) {
                Log.e(TAG, "Error enviando la notificación con SNS", e);
            }
        }).start();
    }
}
