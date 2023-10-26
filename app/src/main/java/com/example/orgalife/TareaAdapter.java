package com.example.orgalife;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {
    private Context context;
    private List<Tarea> tareas;

    public TareaAdapter(Context context, List<Tarea> tareas) {
        this.context = context;
        this.tareas = tareas;
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tarea = tareas.get(position);

        holder.textViewNombreTarea.setText(tarea.getNombre());
        holder.textViewDescripcionTarea.setText(tarea.getDescripcion());
        holder.textViewEtiquetaTarea.setText(tarea.getEtiqueta());

        // Cargar la imagen usando Glide
        Glide.with(context)
                .load(tarea.getImageUrl()) // URL de la imagen
                .fitCenter() // Ajusta la imagen al tamaño del ImageView manteniendo la relación de aspecto
                .placeholder(R.drawable.imagen_predeterminada) // Recurso predeterminado si la imagen no se carga
                .error(R.drawable.imagen_predeterminada) // Recurso a mostrar si hay un error
                .into(holder.imageViewTarea); // ImageView donde se mostrará la imagen
    }

    @Override
    public int getItemCount() {
        return tareas.size();
    }

    static class TareaViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewTarea;
        TextView textViewNombreTarea;
        TextView textViewDescripcionTarea;
        TextView textViewEtiquetaTarea;

        TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewTarea = itemView.findViewById(R.id.imageViewTarea);
            textViewNombreTarea = itemView.findViewById(R.id.textViewNombreTarea);
            textViewDescripcionTarea = itemView.findViewById(R.id.textViewDescripcionTarea);
            textViewEtiquetaTarea = itemView.findViewById(R.id.textViewEtiquetaTarea);
        }
    }
}

