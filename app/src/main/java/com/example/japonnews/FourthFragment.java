package com.example.japonnews;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.japonnews.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FourthFragment extends Fragment {

    private EditText editTextNombre;
    private EditText editTextPassword;
    private EditText editTextNumero, editTextIdentificacion;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ImageView imgProfile;
    private Uri imgUri;
    private Button btnCargarImg;
    private Button btnActualizar, buttonMisPostulaciones, buttonMisPublicaciones, btnLogout;
    private static final int PICK_IMAGE_REQUEST =1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
        auth = FirebaseAuth.getInstance();
        db =FirebaseFirestore.getInstance();

        imgProfile= view.findViewById(R.id.imageView4);
        btnCargarImg=view.findViewById(R.id.button3);
        btnActualizar=view.findViewById(R.id.button4);
        editTextNombre=view.findViewById(R.id.editTextText4);
        editTextIdentificacion=view.findViewById(R.id.editTextText5);
        editTextNumero=view.findViewById(R.id.editTextNumber2);
        editTextPassword=view.findViewById(R.id.editTextContrasena);
        buttonMisPostulaciones=view.findViewById(R.id.buttonMisPostulaciones);
        buttonMisPublicaciones=view.findViewById(R.id.buttonMisPublicaciones);
        btnLogout=view.findViewById(R.id.buttonLogout);
        cargarData();

btnCargarImg.setOnClickListener(v -> cargarImgPerfil());

btnActualizar.setOnClickListener(v -> actualizarData());

buttonMisPublicaciones.setOnClickListener(v -> {
    Intent intent = new Intent(getActivity(), mispublicaciones.class);
    startActivity(intent);
});

buttonMisPostulaciones.setOnClickListener(v -> {
    Intent intent = new Intent(getActivity(), mispostulaciones.class);
    startActivity(intent);
});

btnLogout.setOnClickListener(v -> logout());
        return view;
    }

    private void cargarImgPerfil(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST&&resultCode==RESULT_OK&&data != null&&data.getData()!=null){
            imgUri = data.getData();
            imgProfile.setImageURI(imgUri);
            Log.d("FFragment", "Imagen seleccionada: " + imgUri.toString());
        } else{
            Log.e("FFragment", "No se seleccionó ninguna imagen o hubo un error");
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
                                .addOnSuccessListener(aVoid -> Log.d("FFragment", "Foto de perfil guardada"))
                                .addOnFailureListener(e -> Log.e("FFragment","Error: ", e));
                    }))
                    .addOnFailureListener(e-> Log.e("FFragment", "Error al subir la imagen: ", e));
        }
    }

    private void actualizarData(){
        String userId = auth.getCurrentUser().getUid();
        String nombre = editTextNombre.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String numero = editTextNumero.getText().toString().trim();

        db.collection("usuarios").document(userId)
                .update(
                        "nombre", nombre,
                        "password", password,
                        "telefono", numero
                ).addOnSuccessListener(aVoid ->{
                    Log.d("FFragment", "Los datos se actualizaron" + nombre + password + numero);
                    guardarImg(userId);
                    Toast.makeText(getContext(), "¡Tus datos se actualizaron correctamente!", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Log.e("FFragment", "No se actualizaron los datos");
                    Toast.makeText(getContext(), "No se actualizaron tus datos. Por favor, intente nuevamente.", Toast.LENGTH_SHORT).show();
                }
);}
    private void cargarData(){
        String userId=auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        String nombre = documentSnapshot.getString("nombre");
                        String id = documentSnapshot.getString("id");
                        String telefono = documentSnapshot.getString("telefono");
                        String password = documentSnapshot.getString("password");
                        String fotoPerfilUrl=documentSnapshot.getString("fotoPerfil");

                        editTextNombre.setText(nombre);
                        editTextIdentificacion.setText(id);
                        editTextNumero.setText(telefono);
                        editTextPassword.setText(password);
                        if (fotoPerfilUrl != null && !fotoPerfilUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(fotoPerfilUrl)
                                    .placeholder(R.drawable.japonnews_logo) // Imagen por defecto mientras carga
                                    .error(R.drawable.error) // Imagen en caso de error
                                    .into(imgProfile); // ImageView donde se mostrará la imagen
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FFragment", "No hay datos por cargar"));
    }

    private void logout(){
        auth = FirebaseAuth.getInstance();
        auth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}