package com.chrisley.LM_calculadorpensiones;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

import Estructuras.Pension;
import Estructuras.PensionFireBase;
import Estructuras.Pensionado;
import Estructuras.RecyclerItemHistorial;

public class HIstorialActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ImageButton backbutton;
    TextView CerrarSesion;
    String email;
    ListAdapterHist listAdapterRes;
    Button verCalculoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_h_istorial);

        backbutton = findViewById(R.id.BackButtonHistory);
        TextView tv = findViewById(R.id.IniciasteSesion_TextView);
        CerrarSesion = findViewById(R.id.CerrarSesion_TextView);
        verCalculoButton = findViewById(R.id.VerCaluloButton);
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        email = prefs.getString("email",null);

        tv.setText("Iniciaste sesión como " +  getIntent().getStringExtra("nombre") + " " + getIntent().getStringExtra("apellido"));
        init();
    }

    private void init(){
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        verCalculoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listAdapterRes.getSelected() != null){
                    Pensionado pensionado = listAdapterRes.getSelected();
                    Intent intent = new Intent(HIstorialActivity.this,ResultadoPensionExpress.class);
                    intent.putExtra("pensionado",pensionado);
                    intent.putExtra("historial",true);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Selecciona alguna tarjeta de historial.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Query query = db.collection("Datos_Calculos_Pension_" + email);
        FirestoreRecyclerOptions<PensionFireBase> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<PensionFireBase>()
                .setQuery(query, PensionFireBase.class).build();

        listAdapterRes = new ListAdapterHist(firestoreRecyclerOptions);
        listAdapterRes.notifyDataSetChanged();
        recyclerView.setAdapter(listAdapterRes);

    }



    @Override
    protected void onStart() {
        super.onStart();
        listAdapterRes.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        listAdapterRes.stopListening();
    }

}