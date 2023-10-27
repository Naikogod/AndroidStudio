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

    public interface OnItemClickListener {
        void onItemClick(int position, String nombreDocumento);
    }

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        clickListener = listener;
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

        Glide.with(context)
                .load(tarea.getImageUrl())
                .fitCenter()
                .placeholder(R.drawable.imagen_predeterminada)
                .error(R.drawable.imagen_predeterminada)
                .into(holder.imageViewTarea);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        clickListener.onItemClick(position, tarea.getNombreDocumento());
                    }
                }
            }
        });
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
