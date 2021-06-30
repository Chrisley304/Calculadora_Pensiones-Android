package com.chrisley.LM_calculadorpensiones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import Estructuras.Pensionado;

public class CalculoPension2 extends AppCompatActivity {

    HashMap<String,Double> UMAs = new HashMap<>();
    HashMap<String,Integer> EdadJubilacion = new HashMap<>();
    Button next;
    AutoCompleteTextView menuUMA;
    AutoCompleteTextView menuEdadPens;
    TextInputLayout tfieldSemanascot;
    TextView textViewnombrePensionado;
    String[] AniosUMAS;
    Pensionado pensionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculo_pension2);
        pensionado = (Pensionado) getIntent().getExtras().get("pensionado");
        double[] valores_UMA = { 89.62,86.88,84.49,80.60,75.49,73.04,70.10};

        ImageView logoLM = findViewById(R.id.LogoLM);
        logoLM.setOnClickListener(v -> RegresarMenuPrin());

        ImageButton backButton = findViewById(R.id.BackButtonCalculos2);
        backButton.setOnClickListener(v -> finish());

        tfieldSemanascot = findViewById(R.id.tfieldSemanasCot);
        textViewnombrePensionado = findViewById(R.id.textViewNombrepens);

        textViewnombrePensionado.setText("Introduce los siguientes datos de " + pensionado.getNombre() + " " + pensionado.getApellidos());

        // Para el menu de las UMAs
        menuUMA = findViewById(R.id.menuUMA);
        // Se obtiene el arreglo de String del archivo strings.xml del proyecto
        AniosUMAS = getResources().getStringArray(R.array.AniosUMAs);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item,AniosUMAS);
        menuUMA.setAdapter(arrayAdapter);

        // Se crea la Hash Map con las llaves de las strings Anios y valores solo los valores double
        for (int i = 0; i < AniosUMAS.length; i++) {
            UMAs.put(AniosUMAS[i],valores_UMA[i]);
        }

        //Para el menu de la edad de Jub
        menuEdadPens = findViewById(R.id.menuEdadJub);
        String[] AniosPension = getResources().getStringArray(R.array.AniosJubilacion);
        ArrayAdapter arrAd2 = new ArrayAdapter(this, R.layout.dropdown_item,AniosPension);
        menuEdadPens.setAdapter(arrAd2);

        for (int i = 0; i < AniosPension.length; i++) {
            EdadJubilacion.put(AniosPension[i],60+i);
        }

        menuUMA.addTextChangedListener(validatenotempty);
        menuEdadPens.addTextChangedListener(validatenotempty);

        tfieldSemanascot.getEditText().addTextChangedListener(validatenotempty);

        next = findViewById(R.id.BotonSiguientepen2);
        next.setOnClickListener(v -> Siguiente() );

    }

    private TextWatcher validatenotempty = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String uma = menuUMA.getText().toString();
            String edad = menuEdadPens.getText().toString();
            int semanas = 0;
            if (!tfieldSemanascot.getEditText().getText().toString().isEmpty()) {
                semanas = Integer.parseInt(tfieldSemanascot.getEditText().getText().toString().trim());
            }

            next.setEnabled(!uma.isEmpty() && !edad.isEmpty() && semanas >=500);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!tfieldSemanascot.getEditText().getText().toString().isEmpty()) {
                int sbc = Integer.parseInt(tfieldSemanascot.getEditText().getText().toString());
                if (sbc < 500) {
                    tfieldSemanascot.setError("No puedes ingresar menos de las 500 semanas obligadas.");
                }else {
                    tfieldSemanascot.setError(null);
                }
            }else {
                tfieldSemanascot.setError(null);
            }
        }
    };

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

    private void Siguiente(){

        String seleccionUMA = menuUMA.getText().toString();
        int semanascot = Integer.parseInt(tfieldSemanascot.getEditText().getText().toString().trim());
        String seleccionEdadJub = menuEdadPens.getText().toString();

        double uma_numero = UMAs.get(seleccionUMA);
        int edadjub_numero = EdadJubilacion.get(seleccionEdadJub);

        pensionado.setUMA_pensionado(uma_numero);
        pensionado.setSemanasCotizadas(semanascot);
        pensionado.setEdadJubilacion(edadjub_numero);

        Intent intent = new Intent(this, CalculoPension3.class);
        intent.putExtra("pensionado",pensionado);
        intent.putExtra("email",getIntent().getStringExtra("email"));
        startActivity(intent);
    }

}