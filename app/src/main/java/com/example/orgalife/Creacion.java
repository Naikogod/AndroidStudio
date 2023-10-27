package com.example.orgalife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Creacion extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Uri imageUri;
    private EditText editTextNombre;
    private EditText editTextDescripcion;
    private Spinner spinnerEtiquetas;
    private ArrayAdapter<String> etiquetasAdapter;
    private FirebaseFirestore db;
    private String nombreDocumento; // Agregar como atributo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creacion);

        db = FirebaseFirestore.getInstance();
        nombreDocumento = db.collection("tareas").document().getId(); // Genera un ID único

        imageView = findViewById(R.id.imageView);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextDescripcion = findViewById(R.id.editTextDescripcion);
        spinnerEtiquetas = findViewById(R.id.spinnerEtiquetas);
        etiquetasAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, obtenerEtiquetasPredefinidas());
        spinnerEtiquetas.setAdapter(etiquetasAdapter);

        ImageButton botonSeleccionarFoto = findViewById(R.id.botonSeleccionarFoto);

        botonSeleccionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSeleccionadorDeImagen();
            }
        });

        Button botonCrear = findViewById(R.id.botonCrear);
        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogoConfirmacion();
            }
        });
    }

    private void abrirSeleccionadorDeImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }
    }

    // Añade este método para mostrar un Toast y finalizar la actividad después de 2 segundos.
    private void mostrarToastYTerminar() {
        Toast.makeText(this, "La tarea se registró con éxito", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(new Runnable() {
            public void run() {
                finish(); // Finaliza la actividad después de 2 segundos
            }
        }, 2000); // 2000 ms = 2 segundos
    }

    private void guardarDatosEnFirebase(String nombre, String descripcion, String etiqueta, String imageUrl, String tipoTarea) {
        CollectionReference tareasCollection = db.collection("tareas");
        String creadorId = getCurrentUserId(); // Obtiene el UID del usuario actual

        if (creadorId == null) {
            // El usuario no está autenticado, no se puede guardar la tarea
            return;
        }

        Map<String, Object> tarea = new HashMap<>();
        tarea.put("nombre", nombre);
        tarea.put("descripcion", descripcion);
        tarea.put("etiqueta", etiqueta);
        tarea.put("imageUrl", imageUrl);
        tarea.put("tipoTarea", tipoTarea);
        tarea.put("creadorId", creadorId); // Asigna el UID del usuario como creador
        tarea.put("grupos", new ArrayList<String>()); // Inicializa la lista de "Grupos" como vacía

        // Utiliza el nombre del documento generado
        DocumentReference documentReference = tareasCollection.document(nombreDocumento);

        documentReference.set(tarea) // Usar "set" en lugar de "add"
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La tarea se ha guardado con éxito en Firestore.
                        mostrarToastYTerminar();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // Manejar errores si ocurren al guardar los datos en Firestore.
                    }
                });
    }

    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de crear la tarea?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nombre = editTextNombre.getText().toString();
                String descripcion = editTextDescripcion.getText().toString();
                String etiqueta = spinnerEtiquetas.getSelectedItem().toString();

                RadioGroup radioGroup = findViewById(R.id.TipoTarea);
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                String tipoTarea = selectedRadioButton.getText().toString();

                subirArchivoAFirebase(imageUri, nombre, descripcion, etiqueta, tipoTarea);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada, simplemente cierra el diálogo.
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<String> obtenerEtiquetasPredefinidas() {
        List<String> etiquetas = new ArrayList<>();
        etiquetas.add("Trabajo");
        etiquetas.add("Hobby");
        etiquetas.add("Deporte");
        etiquetas.add("Alimentación");
        // Puedes agregar más etiquetas predefinidas aquí.
        return etiquetas;
    }

    private void subirArchivoAFirebase(Uri fileUri, String nombre, String descripcion, String etiqueta, String tipoTarea) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String timestamp = String.valueOf(System.currentTimeMillis());
        StorageReference fileRef = storageRef.child("Imagenes/" + timestamp + ".jpg");

        fileRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        guardarDatosEnFirebase(nombre, descripcion, etiqueta, imageUrl, tipoTarea);
                    });
                })
                .addOnFailureListener(e -> {
                    // Manejar errores si ocurren al subir la imagen.
                });
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null; // No hay usuario autenticado
        }
    }
}
