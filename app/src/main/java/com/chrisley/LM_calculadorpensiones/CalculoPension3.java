package com.chrisley.LM_calculadorpensiones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import Estructuras.Pensionado;

public class CalculoPension3 extends AppCompatActivity {

    AutoCompleteTextView menuhijos;
    AutoCompleteTextView menupadres;
    RadioButton botonSiConyuge;
    RadioButton botonNoConyuge;
    Button next;
    TextView textpapas;
    Pensionado pensionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculo_pension3);
        pensionado = (Pensionado) getIntent().getExtras().get("pensionado");

        ImageView logoLM = findViewById(R.id.LogoLM);
        logoLM.setOnClickListener(v -> RegresarMenuPrin());

        menuhijos = findViewById(R.id.menuhijos);
        menupadres = findViewById(R.id.menupadre);
        textpapas = findViewById(R.id.textViewnumpapas);
        botonSiConyuge = findViewById(R.id.radioSiconyug);
        botonNoConyuge = findViewById(R.id.radioNoconyug);

        botonSiConyuge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menupadres.setText("0");
                menupadres.setEnabled(false);
                textpapas.setTextColor(ContextCompat.getColor(CalculoPension3.this,R.color.grey_off));
                next.setEnabled(true);
            }
        });
        botonNoConyuge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nhijos = menuhijos.getText().toString();
                if(nhijos.equals("0")){
                    String[] num_papas = {"0","1","2"};
                    menupadres.setAdapter(new ArrayAdapter(CalculoPension3.this, R.layout.dropdown_item,num_papas));
                    menupadres.setEnabled(true);
                    textpapas.setTextColor(ContextCompat.getColor(CalculoPension3.this,R.color.pure_black));
                }else{
                    menupadres.setText("0");
                    textpapas.setTextColor(ContextCompat.getColor(CalculoPension3.this,R.color.grey_off));
                    menupadres.setEnabled(false);
                    next.setEnabled(true);
                }
                next.setEnabled(true);
            }
        });

        next = findViewById(R.id.BotonSiguientepen3);

        ImageButton backButton = findViewById(R.id.BackButtonCalculos3);
        backButton.setOnClickListener(v -> finish());
        String[] hijos_numeros = new String[11];

        for (int i = 0; i <= 10; i++) {
            hijos_numeros[i] = String.valueOf(i);
        }


        ArrayAdapter adapHijos = new ArrayAdapter(this, R.layout.dropdown_item,hijos_numeros);
        menuhijos.setAdapter(adapHijos);

        String[] num_papas = {"0","1","2"};
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.dropdown_item,num_papas);
        menupadres.setAdapter(adapter);

        menuhijos.addTextChangedListener(validatePadresandNotEmpty);

        next.setOnClickListener(v -> Siguiente());
    }

    void RegresarMenuPrin(){
        // Se redirige a la actividad de Menu principal
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Regresar al menu principal");
        builder.setMessage("¿Estas seguro que quieres regresar al menu principal?, no se guardaran los datos ingresados.");
        // Boton para cerrar sesion
        builder.setPositiveButton("Regresar al menu principal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(),MenuPrincipal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private TextWatcher validatePadresandNotEmpty = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nhijos = menuhijos.getText().toString();
            if(nhijos.equals("0") && botonNoConyuge.isChecked()){
                String[] num_papas = {"0","1","2"};
                menupadres.setAdapter(new ArrayAdapter(CalculoPension3.this, R.layout.dropdown_item,num_papas));
                menupadres.setEnabled(true);
                textpapas.setTextColor(ContextCompat.getColor(CalculoPension3.this,R.color.pure_black));
            }else{
                menupadres.setText("0");
                menupadres.setEnabled(false);
                textpapas.setTextColor(ContextCompat.getColor(CalculoPension3.this,R.color.grey_off));
            }
            next.setEnabled(botonSiConyuge.isChecked() || botonNoConyuge.isChecked());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void Siguiente (){
        boolean conyuge = botonSiConyuge.isChecked();
        int nhijos = Integer.parseInt(menuhijos.getText().toString());
        int padres = Integer.parseInt(menupadres.getText().toString());

        pensionado.setConyuge(conyuge);
        pensionado.setHijos(nhijos);
        pensionado.setPadres(padres);

        Intent intent = new Intent(CalculoPension3.this, ResultadoPensionExpress.class);
        intent.putExtra("pensionado",pensionado);
        intent.putExtra("email",getIntent().getStringExtra("email"));
        intent.putExtra("historial",false);
        startActivity(intent);

    }
}