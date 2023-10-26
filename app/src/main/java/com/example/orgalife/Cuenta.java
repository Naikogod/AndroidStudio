package com.example.orgalife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cuenta extends AppCompatActivity {

    private ImageView imageViewPerfil;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);

        // Obtener la referencia al Firebase Storage.
        storageRef = FirebaseStorage.getInstance().getReference().child("Imagenes/Iconos");

        imageViewPerfil = findViewById(R.id.imageViewPerfil);

        Button botonSeleccionarIcono = findViewById(R.id.botonSeleccionarIcono);
        botonSeleccionarIcono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoSeleccionarIcono();
            }
        });
        mostrarNombreUsuario();
    }

    private void mostrarNombreUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                // Extrae el nombre de usuario de la parte local del correo electrónico.
                String nombre = email.substring(0, email.indexOf("@"));

                // Muestra el nombre en un TextView (reemplaza 'textViewNombre' con el ID de tu TextView).
                TextView textViewNombre = findViewById(R.id.textViewNombre);
                textViewNombre.setText("Nombre: " + nombre);
            }
        }
    }


    private void mostrarDialogoSeleccionarIcono() {
        // Obtén la lista de nombres de imágenes disponibles.
        List<String> nombresImagenes = obtenerNombresImagenes();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar una imagen de perfil");

        List<ImageView> imageViews = new ArrayList<>();
        for (String nombreImagen : nombresImagenes) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            cargarImagenDesdeFirebaseStorage(nombreImagen, imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageUrl = obtenerUrlDeImageView(imageView);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        String uid = user.getUid();
                        guardarImageUrlEnFirestore(uid, imageUrl);
                    }
                    Toast.makeText(Cuenta.this, "Imagen seleccionada: " + nombreImagen, Toast.LENGTH_SHORT).show();
                    builder.create().dismiss();
                }
            });

            imageViews.add(imageView);
        }

        ScrollView scrollView = new ScrollView(this);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (ImageView imageView : imageViews) {
            linearLayout.addView(imageView);
        }

        scrollView.addView(linearLayout);
        builder.setView(scrollView);

        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // El usuario canceló la selección
            }
        });

        builder.show();
    }

    private String obtenerUrlDeImageView(ImageView imageView) {
        Object tag = imageView.getTag();

        if (tag != null) {
            return tag.toString();
        } else {
            return "";
        }
    }

    private void cargarImagenDesdeFirebaseStorage(String nombreImagen, final ImageView imageView) {
        StorageReference imageRef = storageRef.child(nombreImagen);

        imageRef.getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            String imageUrl = uri.toString();
                            Glide.with(Cuenta.this)
                                    .load(imageUrl)
                                    .into(imageView);
                            // Establece la URL de la imagen como etiqueta en el ImageView
                            imageView.setTag(imageUrl);
                        } else {
                            Toast.makeText(Cuenta.this, "Error al obtener la URL de la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private List<String> obtenerNombresImagenes() {
        List<String> nombres = new ArrayList<>();
        nombres.add("calabaza.png");
        nombres.add("demonio.png");
        nombres.add("dracula.png");
        nombres.add("ectoplasma.png");
        nombres.add("fantasma.png");
        nombres.add("gato.png");
        nombres.add("globo-ocular.png");
        nombres.add("guadana.png");
        nombres.add("hombre-lobo.png");
        nombres.add("mano-zombi.png");
        nombres.add("momia.png");
        nombres.add("parca.png");
        nombres.add("sombrero-pirata.png");
        nombres.add("vampiro.png");
        nombres.add("zombi.png");
        // Agrega más nombres de imágenes si es necesario.
        return nombres;
    }

    private void guardarImageUrlEnFirestore(String uid, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(uid);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // El documento ya existe, actualiza un campo del documento (o no hagas nada).
                        actualizarColeccion(uid, imageUrl);
                    } else {
                        // El documento no existe, crea un nuevo documento.
                        crearColeccion(uid, imageUrl);
                    }
                } else {
                    // Manejar errores si ocurren al verificar la existencia del documento.
                    Toast.makeText(Cuenta.this, "Error al verificar la existencia del documento", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void crearColeccion(String uid, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(uid);

        Map<String, Object> userData = new HashMap<>();
        userData.put("imageUrl", imageUrl);

        userRef.set(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La colección y el documento se han creado con éxito.
                        Toast.makeText(Cuenta.this, "Colección y documento creados en Firestore", Toast.LENGTH_SHORT).show();
                        cargarImagenDesdeURL(imageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores si ocurren al crear la colección y el documento.
                        Toast.makeText(Cuenta.this, "Error al crear la colección y el documento en Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void actualizarColeccion(String uid, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("usuarios").document(uid);

        userRef.update("imageUrl", imageUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // El campo "imageUrl" se ha actualizado con éxito en Firestore.
                        Toast.makeText(Cuenta.this, "Campo 'imageUrl' actualizado en Firestore", Toast.LENGTH_SHORT).show();
                        cargarImagenDesdeURL(imageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Manejar errores si ocurren al actualizar el campo "imageUrl".
                        Toast.makeText(Cuenta.this, "Error al actualizar el campo 'imageUrl' en Firestore", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void cargarImagenDesdeURL(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(imageViewPerfil);
    }
}
