package com.example.orgalife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class Comentarios extends Fragment {

    public Comentarios() {
        // Constructor vacío requerido.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comentarios, container, false);


        FloatingActionButton botonCrearComentario = v.findViewById(R.id.crearComentario);
        botonCrearComentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Antes de mostrar el cuadro de diálogo para crear un comentario, asegúrate de que la colección "Comentarios" exista.
                crearColeccionComentarios();
                mostrarDialogoCrearComentario();
            }
        });

        return v;
    }

    // Método para crear la colección "Comentarios" en Firestore si no existe
    private void crearColeccionComentarios() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference comentariosCollection = db.collection("Comentarios");

        comentariosCollection
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().isEmpty()) {
                        // La colección "Comentarios" no existe, por lo que la creamos.
                        comentariosCollection
                                .document("dummyDocument")
                                .set(new HashMap<>()) // Puedes usar un documento vacío o con datos mínimos
                                .addOnCompleteListener(result -> {
                                    if (result.isSuccessful()) {
                                        // La colección "Comentarios" ha sido creada.
                                    } else {
                                        // Maneja posibles errores al crear la colección.
                                    }
                                });
                    }
                });
    }

    // Método para mostrar el cuadro de diálogo para crear un comentario
    private void mostrarDialogoCrearComentario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Nuevo Comentario");
        View view = getLayoutInflater().inflate(R.layout.dialog_crear_comentario, null);
        builder.setView(view);

        EditText editTextComentario = view.findViewById(R.id.editTextComentario);

        builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Recoge el comentario ingresado por el usuario desde editTextComentario.
                String comentario = editTextComentario.getText().toString();

                // Comprueba que el comentario no esté vacío.
                if (!TextUtils.isEmpty(comentario)) {
                    // Obtiene el nombre del usuario actual desde Firebase Authentication.
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String nombreUsuario = currentUser != null ? currentUser.getEmail().split("@")[0] : "NombreDesconocido";

                    // Obtiene el nombre del documento (tarea) desde los argumentos del fragmento.
                    Bundle args = getArguments();
                    String nombreDocumento = args != null ? args.getString("nombreDocumento", "NombreDesconocido") : "NombreDesconocido";

                    // Guarda el comentario en Firestore.
                    guardarComentarioEnFirestore(nombreUsuario, nombreDocumento, comentario);
                } else {
                    // Muestra un mensaje de error al usuario indicando que el comentario está vacío.
                    Toast.makeText(requireContext(), "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Cierra el cuadro de diálogo
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Método para guardar el comentario en Firestore
    private void guardarComentarioEnFirestore(String nombreUsuario, String nombreDocumento, String comentario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference comentariosCollection = db.collection("Comentarios");

        // Crea un nuevo documento para el comentario con un ID automático.
        DocumentReference nuevoComentario = comentariosCollection.document();

        // Crea un mapa con los datos del comentario.
        Map<String, Object> datosComentario = new HashMap<>();
        datosComentario.put("nombreUsuario", nombreUsuario);
        datosComentario.put("nombreDocumento", nombreDocumento);
        datosComentario.put("comentario", comentario);

        // Guarda el mapa de datos en Firestore.
        nuevoComentario.set(datosComentario)
                .addOnSuccessListener(aVoid -> {
                    // El comentario se ha guardado exitosamente.
                    Toast.makeText(requireContext(), "Comentario enviado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Maneja posibles errores al guardar el comentario.
                    Toast.makeText(requireContext(), "Error al enviar el comentario", Toast.LENGTH_SHORT).show();
                });
    }
}

