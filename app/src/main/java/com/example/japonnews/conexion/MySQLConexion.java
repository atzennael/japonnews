package com.example.japonnews.conexion;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MySQLConexion {

    private static final String URL_REGISTRO = "http://10.0.2.2/API REST/registro_usuario.php"; // Ajusta la ruta según tu servidor

    public static void registrarUsuario(Context context, String firebaseUID, String nombre, String email, String fotoPerfil) {
        RequestQueue queue = Volley.newRequestQueue(context);

        Map<String, String> params = new HashMap<>();
        params.put("firebase_uid", firebaseUID);
        params.put("nombre", nombre);
        params.put("email", email);
        params.put("foto_perfil", fotoPerfil != null ? fotoPerfil : "");

        JSONObject jsonObject = new JSONObject(params);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_REGISTRO,
                jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean success = response.getBoolean("success");
                            String message = response.getString("message");

                            if (success) {
                                Log.d("Registro", "Usuario registrado correctamente: " + message);
                            } else {
                                Log.e("Registro", "Error en el registro: " + message);
                            }
                        } catch (JSONException e) {
                            Log.e("Registro", "Error al procesar la respuesta JSON", e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Registro", "Error en la conexión: " + error.getMessage());
                    }
                }
        );

        queue.add(request);
    }
}

