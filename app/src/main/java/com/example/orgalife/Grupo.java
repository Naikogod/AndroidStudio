package com.example.orgalife;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orgalife.Tarea;
import com.example.orgalife.TareaAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Grupo extends Fragment implements TareaAdapter.OnItemClickListener {
    // Interfaz Para comunicar
    public interface OnTareaClickListener {
        void onTareaClicked(Tarea tarea);
    }

    private OnTareaClickListener tareaClickListener;
    private RecyclerView recyclerView;
    private TareaAdapter tareaAdapter;
    private List<Tarea> tareas;
    private FirebaseFirestore db;
    private String currentUserId;

    public Grupo() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_grupo, container, false);

        recyclerView = v.findViewById(R.id.ListaGrupo);
        tareas = new ArrayList<>();
        tareaAdapter = new TareaAdapter(requireContext(), tareas);
        tareaAdapter.setOnItemClickListener(this); // Establece el listener en el adaptador
        recyclerView.setAdapter(tareaAdapter);

        // Obtén el UID del usuario actual
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUserId = currentUser.getUid();
        } else {
            // El usuario no está autenticado. Maneja esta situación según tus necesidades.
        }

        db = FirebaseFirestore.getInstance();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        obtenerTareasGrupos(); // Obtener tareas según la pertenencia a grupos

        return v;
    }

    private void obtenerTareasGrupos() {
        CollectionReference tareasRef = db.collection("tareas");
        Query query = tareasRef.whereArrayContains("grupos", currentUserId);

        query.addSnapshotListener((value, error) -> {
            if (error != null) {
                // Manejar errores, si los hay.
                return;
            }

            tareas.clear(); // Limpiar la lista de tareas antes de agregar las nuevas.

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
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            tareaClickListener = (OnTareaClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " debe implementar la interfaz OnTareaClickListener");
        }
    }

    @Override
    public void onItemClick(int position, String nombreDocumento) {
        Tarea tarea = tareas.get(position);

        if (tareaClickListener != null) {
            tareaClickListener.onTareaClicked(tarea);
        }
    }
}


