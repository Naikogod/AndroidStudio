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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class Completadas extends Fragment {

    private RecyclerView recyclerView;
    private TareaAdapter tareaAdapter;
    private List<Tarea> tareas;
    private FirebaseFirestore db;
    private String currentUserUid;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_completadas, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.ListaCompletadas);
        tareas = new ArrayList<>();
        tareaAdapter = new TareaAdapter(requireContext(), tareas);

        recyclerView.setAdapter(tareaAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        // Obtener el UID del usuario actual
        currentUserUid = getCurrentUserUid();

        // Consulta para mostrar tareas privadas creadas por el usuario actual
        obtenerTareasPrivadas();

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

    private void obtenerTareasPrivadas() {
        CollectionReference tareasRef = db.collection("tareas");

        tareasRef.whereEqualTo("tipoTarea", "Privado") // Filtrar tareas privadas
                .whereEqualTo("creadorId", currentUserUid) // Filtrar por UID del creador (usuario actual)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Manejar cualquier error que pueda ocurrir, Normalmente utilize toast
                            return;
                        }

                        tareas.clear(); // Limpiar la lista de tareas antes de agregar las nuevas.

                        for (QueryDocumentSnapshot document : value) {
                            String nombre = document.getString("nombre");
                            String descripcion = document.getString("descripcion");
                            String etiqueta = document.getString("etiqueta");
                            String imageUrl = document.getString("imageUrl");

                            // Asegúrate de obtener el nombre del documento también
                            String nombreDocumento = document.getId();

                            Tarea tarea = new Tarea(nombre, descripcion, etiqueta, imageUrl, nombreDocumento);
                            tareas.add(tarea);
                        }

                        tareaAdapter.notifyDataSetChanged();
                    }
                });
    }

    private String getCurrentUserUid() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null; // No hay usuario autenticado
        }
    }
}
