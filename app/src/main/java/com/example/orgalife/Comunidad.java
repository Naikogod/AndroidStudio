package com.example.orgalife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
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

        recyclerView = v.findViewById(R.id.ListaComunidad); // Mueve esta línea arriba

        tareas = new ArrayList<>();
        tareaAdapter = new TareaAdapter(requireContext(), tareas);
        recyclerView.setAdapter(tareaAdapter);

        tareaAdapter.setOnItemClickListener(new TareaAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mostrarDialogo(tareas.get(position).getNombre());
            }
        });

        db = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        obtenerTareasPublicas(); // Filtrar tareas públicas

        FloatingActionButton bt = v.findViewById(R.id.BotonFlotante1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Iniciar la actividad Creacion
                Intent i = new Intent(getActivity(), Creacion.class);
                startActivity(i);
            }
        });

        return v;
    }

    private void obtenerTareasPublicas() {
        CollectionReference tareasRef = db.collection("tareas");
        tareasRef.whereEqualTo("tipoTarea", "Publico") // Filtrar tareas públicas
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Manejar cualquier error que pueda ocurrir.
                            return;
                        }

                        tareas.clear(); // Limpiar la lista de tareas antes de agregar las nuevas.

                        for (QueryDocumentSnapshot document : value) {
                            String nombre = document.getString("nombre");
                            String descripcion = document.getString("descripcion");
                            String etiqueta = document.getString("etiqueta");
                            String imageUrl = document.getString("imageUrl");

                            Tarea tarea = new Tarea(nombre, descripcion, etiqueta, imageUrl);
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
                // El usuario eligió "Sí", mostrar Toast con el nombre de la tarea
                Toast.makeText(requireContext(), "Nombre de la tarea: " + nombreTarea, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // El usuario eligió "No", cierra el diálogo
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
