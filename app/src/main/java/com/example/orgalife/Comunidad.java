package com.example.orgalife;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.orgalife.R;
import com.example.orgalife.Tarea;
import com.example.orgalife.TareaAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class Comunidad extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private TareaAdapter tareaAdapter;
    private List<Tarea> tareas;
    private FirebaseFirestore db;

    private int selectedPosition = -1;
    private String selectedDocument;

    public Comunidad() {
    }

    public static Comunidad newInstance(String param1, String param2) {
        Comunidad fragment = new Comunidad();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comunidad, container, false);

        recyclerView = v.findViewById(R.id.ListaComunidad);
        tareas = new ArrayList<>();
        tareaAdapter = new TareaAdapter(requireContext(), tareas);
        recyclerView.setAdapter(tareaAdapter);

        tareaAdapter.setOnItemClickListener(new TareaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, String nombreDocumento) {
                selectedPosition = position;
                selectedDocument = nombreDocumento;
                mostrarDialogo(tareas.get(position).getNombre());
            }
        });

        db = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        obtenerTareasPublicas();

        return v;
    }

    private void obtenerTareasPublicas() {
        CollectionReference tareasRef = db.collection("tareas");
        tareasRef.whereEqualTo("tipoTarea", "Publico")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        tareas.clear();

                        for (QueryDocumentSnapshot document : value) {
                            String nombre = document.getString("nombre");
                            String descripcion = document.getString("descripcion");
                            String etiqueta = document.getString("etiqueta");
                            String imageUrl = document.getString("imageUrl");
                            String nombreDocumento = document.getId();

                            Tarea tarea = new Tarea(nombre, descripcion, etiqueta, imageUrl, nombreDocumento);
                            tareas.add(tarea);
                        }

                        tareaAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void mostrarDialogo(final String nombreTarea) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("¿Se quiere añadir al Grupo?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedPosition != -1) {
                    int position = selectedPosition;
                    String nombreDocumento = selectedDocument;
                    DocumentReference tareaRef = db.collection("tareas").document(nombreDocumento);

                    // Agrega el UID del usuario a la lista "Grupos" de la tarea
                    String uidUsuario = getCurrentUserId();
                    if (uidUsuario != null) {
                        tareaRef.update("grupos", FieldValue.arrayUnion(uidUsuario))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(requireContext(), "Tarea actualizada con éxito", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(requireContext(), "Error al actualizar la tarea", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private String getCurrentUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
        }
    }
}

