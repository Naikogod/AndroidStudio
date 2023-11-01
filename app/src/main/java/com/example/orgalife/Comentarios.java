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

        // Obtén el argumento (nombreDocumento) del fragmento
        Bundle args = getArguments();
        if (args != null) {
            String nombreDocumento = args.getString("nombreDocumento");
            if (nombreDocumento != null) {
                // Encuentra el TextView en tu diseño
                TextView textViewTaskName = v.findViewById(R.id.textViewTaskName);

                // Muestra el nombre del documento en el TextView
                textViewTaskName.setText(nombreDocumento);
            }
        }

        return v;
    }
}

