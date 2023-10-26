package com.example.orgalife;

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

        db = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.ListaComunidad);
        tareas = new ArrayList<>();
        tareaAdapter = new TareaAdapter(requireContext(), tareas);

        recyclerView.setAdapter(tareaAdapter);
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
}