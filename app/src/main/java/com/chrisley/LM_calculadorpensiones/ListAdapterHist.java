package com.chrisley.LM_calculadorpensiones;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Estructuras.Pension;
import Estructuras.PensionFireBase;
import Estructuras.Pensionado;
import Estructuras.RecyclerItemHistorial;

public class ListAdapterHist extends FirestoreRecyclerAdapter<PensionFireBase,ListAdapterHist.ViewHolder> {
    private Context context;
    FirebaseFirestore db;
    String emailLogged;
    NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance();
    ArrayList<PensionFireBase> listaPensionFirebase = new ArrayList<>();

    private int checkedPosition = -1;

    public ListAdapterHist(FirestoreRecyclerOptions<PensionFireBase> options) {
        super(options);
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ListAdapterHist.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial,parent,false);
        return new ListAdapterHist.ViewHolder(view);
    }



    @Override
    protected void onBindViewHolder(ListAdapterHist.ViewHolder holder, int position,PensionFireBase item) {
        holder.bind(item);
        listaPensionFirebase.add(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView nombrecomp, sbc,pension,edadjub;
        ImageView delete;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.nombrecomp = itemView.findViewById(R.id.NombreCompletoTextView);
            this.edadjub = itemView.findViewById(R.id.JubilacionTextView);
            this.sbc = itemView.findViewById(R.id.SBCTextView);
            this.pension = itemView.findViewById(R.id.PensionTextView);
            this.delete = itemView.findViewById(R.id.trashImageView);
            this.cardView = itemView.findViewById(R.id.HistItemCardView);
        }

        public void bind(PensionFireBase pensionado){
            DocumentSnapshot pensionadoDocument = getSnapshots().getSnapshot(getAdapterPosition());
            final String id = pensionadoDocument.getId();
            String nombreCompleto = pensionado.getNombre()+ " " +pensionado.getApellidos();

            SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE);
            emailLogged = prefs.getString("email",null);
            nombrecomp.setText(nombreCompleto);
            edadjub.setText("Edad jubilación: "+pensionado.getEdadJubilacion());
            sbc.setText("S.B.C: "+formatoMoneda.format(pensionado.getSBC()));
            pension.setText("Pensión: "+formatoMoneda.format(pensionado.getPENSION_FINAL()));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("¿Eliminar los datos de " + nombreCompleto + " ?");
                    builder.setMessage("Este cambio no se puede deshacer.");
                    builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.collection("Datos_Calculos_Pension_" + emailLogged).document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context,"Los datos se eliminaron correctamente",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    builder.setNegativeButton("Cancelar",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            if(checkedPosition == -1){
                cardView.setStrokeColor(context.getColor(R.color.pure_white));
            }else{
                if(checkedPosition == getAdapterPosition()){
                    cardView.setStrokeColor(context.getColor(R.color.pure_black));
                }else{
                    cardView.setStrokeColor(context.getColor(R.color.pure_white));
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardView.setStrokeColor(context.getColor(R.color.pure_black));
                    if(checkedPosition != getAdapterPosition()){
                        notifyItemChanged(checkedPosition);
                        checkedPosition = getAdapterPosition();
                    }
                }
            });

        }// Fin bind()

    }// Fin ViewHolder class

    public Pensionado getSelected(){
        if(checkedPosition != -1){
            Pensionado pensionado = new Pensionado();
            PensionFireBase itemSelected = listaPensionFirebase.get(checkedPosition);
            pensionado.setPENSION_FINAL(itemSelected.getPENSION_FINAL());
            pensionado.setSBC(itemSelected.getSBC());
            pensionado.setUMA_pensionado(itemSelected.getUMA_pensionado());
            pensionado.setApellidos(itemSelected.getApellidos());
            pensionado.setConyuge(itemSelected.getConyuge());
            pensionado.setEdadJubilacion((int) itemSelected.getEdadJubilacion());
            pensionado.setHijos((int) itemSelected.getHijos());
            pensionado.setNombre( itemSelected.getNombre());
            pensionado.setPadres((int) itemSelected.getPadres());
            pensionado.setSemanasCotizadas((int) itemSelected.getSemanasCotizadas());
            return pensionado;
        }else{
            return null;
        }
    }

}// Fin ListAdapterHis class
