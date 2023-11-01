package com.example.orgalife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ComentarioAdapter extends RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder> {
    private List<Comentario> comentarios;

    public ComentarioAdapter(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    @NonNull
    @Override
    public ComentarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comentario, parent, false);
        return new ComentarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComentarioViewHolder holder, int position) {
        Comentario comentario = comentarios.get(position);
        holder.bind(comentario);
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public class ComentarioViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewNombreUsuario;
        private TextView textViewComentario;

        public ComentarioViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombreUsuario = itemView.findViewById(R.id.textViewNombreUsuario);
            textViewComentario = itemView.findViewById(R.id.textViewComentario);
        }

        public void bind(Comentario comentario) {
            textViewNombreUsuario.setText(comentario.getNombreUsuario());
            textViewComentario.setText(comentario.getComentario());
        }
    }

    public void setComentarios(List<Comentario> nuevosComentarios) {
        comentarios.clear(); // Limpia los comentarios existentes
        comentarios.addAll(nuevosComentarios); // Agrega los nuevos comentarios
        notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
    }

}
