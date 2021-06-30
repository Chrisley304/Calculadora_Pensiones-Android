package com.chrisley.LM_calculadorpensiones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Estructuras.RecyclerItem;

public class ListAdapterRes extends RecyclerView.Adapter<ListAdapterRes.ViewHolder> {
    private List<RecyclerItem>  mData;
    private LayoutInflater mInflater;
    private Context context;

    public ListAdapterRes(List<RecyclerItem> mData, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = mData;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @NonNull
    @Override
    public ListAdapterRes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_res_pension,null);
        return new ListAdapterRes.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(mData.get(position));
    }

    public void setItems(List<RecyclerItem> items){
        mData = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iconImage;
        TextView titulo, contenido;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iconImage = itemView.findViewById(R.id.IconimageviewCard);
            this.titulo = itemView.findViewById(R.id.textViewTituloCard);
            this.contenido = itemView.findViewById(R.id.textViewContenidoCard);
        }

        void bindData(final RecyclerItem item){
            iconImage.setImageResource(item.getIcon_path());
            titulo.setText(item.getTitulo());
            contenido.setText(item.getContenido());
        }
    }
}
