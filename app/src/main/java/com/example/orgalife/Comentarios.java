package com.example.orgalife;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class Comentarios extends Fragment {

    public Comentarios() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_comentarios, container, false);

        // Obtén los argumentos (nombre de la tarea) del fragmento
        Bundle args = getArguments();
        if (args != null) {
            String nombreTarea = args.getString("nombreTarea");
            if (nombreTarea != null) {
                // Encuentra el TextView en tu diseño
                TextView textViewTaskName = v.findViewById(R.id.textViewTaskName);

                // Muestra el nombre de la tarea en el TextView
                textViewTaskName.setText(nombreTarea);
            }
        }

        return v;
    }
}
