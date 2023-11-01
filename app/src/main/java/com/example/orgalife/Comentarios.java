package com.example.orgalife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Comentarios extends Fragment {

    private ComentarioAdapter comentarioAdapter;
    private String nombreDocumento; // Variable para almacenar el nombre del documento de la tarea

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
                crearColeccionComentarios();
                mostrarDialogoCrearComentario();
            }
        });

        RecyclerView recyclerView = v.findViewById(R.id.recyclerViewComentarios);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        comentarioAdapter = new ComentarioAdapter(new ArrayList<>());
        recyclerView.setAdapter(comentarioAdapter);

        Bundle args = getArguments();
        if (args != null) {
            nombreDocumento = args.getString("nombreDocumento");
        }

        if (nombreDocumento != null) {
            cargarComentariosDeLaTarea();
        } else {
            Log.d("Comentarios", "Nombre del documento no proporcionado");
        }

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
                        comentariosCollection
                                .document("dummyDocument")
                                .set(new HashMap<>())
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
                String comentario = editTextComentario.getText().toString();

                if (!TextUtils.isEmpty(comentario)) {
                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    String nombreUsuario = currentUser != null ? currentUser.getEmail().split("@")[0] : "NombreDesconocido";

                    guardarComentarioEnFirestore(nombreUsuario, nombreDocumento, comentario);
                } else {
                    Toast.makeText(requireContext(), "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cerrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Método para guardar el comentario en Firestore
    private void guardarComentarioEnFirestore(String nombreUsuario, String nombreDocumento, String comentario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference comentariosCollection = db.collection("Comentarios");
        DocumentReference nuevoComentario = comentariosCollection.document();

        Map<String, Object> datosComentario = new HashMap<>();
        datosComentario.put("nombreUsuario", nombreUsuario);
        datosComentario.put("nombreDocumento", nombreDocumento);
        datosComentario.put("comentario", comentario);

        nuevoComentario.set(datosComentario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Comentario enviado con éxito", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al enviar el comentario", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para cargar y mostrar los comentarios de la tarea con el nombreDocumento
    // Método para cargar y mostrar los comentarios de la tarea con el nombreDocumento
    private void cargarComentariosDeLaTarea() {
        List<Comentario> comentarios = new ArrayList<>();
        Log.d("Comentarios", "Nombre del documento a buscar: " + nombreDocumento);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference comentariosCollection = db.collection("Comentarios");

        comentariosCollection
                .whereEqualTo("nombreDocumento", nombreDocumento)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        comentarios.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nombreUsuario = document.getString("nombreUsuario");
                            String comentario = document.getString("comentario");
                            comentarios.add(new Comentario(nombreUsuario, comentario));
                            Log.d("Comentarios", "Comentario agregado: " + comentario);
                        }
                        // Actualiza la lista de comentarios en el adaptador
                        // Actualiza la lista de comentarios en el adaptador
                        comentarioAdapter.setComentarios(comentarios);
                        comentarioAdapter.notifyDataSetChanged();
                        Log.d("Comentarios", "Notificando al adaptador que los datos han cambiado");
                    } else {
                        Toast.makeText(requireContext(), "Error al obtener comentarios", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
