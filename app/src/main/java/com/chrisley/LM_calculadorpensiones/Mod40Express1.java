package com.chrisley.LM_calculadorpensiones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputLayout;

import Estructuras.Pensionado;

public class Mod40Express1 extends AppCompatActivity {

    ImageButton backButton;
    Button bottoncalcular;
    TextInputLayout inputNombre;
    TextInputLayout inputApellido;
    TextInputLayout inputSBC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod40_express1);

        ImageView logoLM = findViewById(R.id.LogoLM);
        logoLM.setOnClickListener(v -> RegresarMenuPrin());

        backButton = findViewById(R.id.BackButtonCalculos);
        backButton.setOnClickListener(v -> finish());

        inputNombre = findViewById(R.id.tfieldNombresM40);
        inputNombre.getEditText().addTextChangedListener(validatenotempty);

        inputApellido = findViewById(R.id.tfieldApellidosM40);
        inputApellido.getEditText().addTextChangedListener(validatenotempty);

        inputSBC = findViewById(R.id.tfieldSBCM40);
        inputSBC.getEditText().addTextChangedListener(validatenotempty);

        bottoncalcular = findViewById(R.id.BotonCalcular_M40);
        bottoncalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Mod40Express1.this, ResultadoExpressMod40.class);
                intent.putExtra("pensionado", new Pensionado(inputNombre.getEditText().getText().toString(),inputApellido.getEditText().getText().toString(), Double.parseDouble(inputSBC.getEditText().getText().toString())));
                startActivity(intent);
            }
        });

    }

    private TextWatcher validatenotempty = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nombre = inputNombre.getEditText().getText().toString();
            String apellido = inputApellido.getEditText().getText().toString();
            double sbc = 0;
            if(!inputSBC.getEditText().getText().toString().isEmpty()){
                sbc = Double.parseDouble(inputSBC.getEditText().getText().toString());
            }

            bottoncalcular.setEnabled(!nombre.isEmpty() && !apellido.isEmpty() && sbc != 0);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!inputSBC.getEditText().getText().toString().isEmpty()) {
                double sbc = Double.parseDouble(inputSBC.getEditText().getText().toString());
                if (sbc <= 0) {
                    inputSBC.setError("El sueldo base de cotización no puede ser menor o igual a 0.");
                }
            } else {
                inputSBC.setError(null);
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

}