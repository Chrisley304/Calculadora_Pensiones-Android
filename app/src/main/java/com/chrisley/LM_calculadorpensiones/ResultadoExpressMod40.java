package com.chrisley.LM_calculadorpensiones;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Objects;

import Estructuras.Pensionado;

public class ResultadoExpressMod40 extends AppCompatActivity {

    ImageButton backButton;
    TextInputLayout tfieldSBC;
    TextInputLayout tfieldAnios;
    TextView TituloNombre;
    ImageView increaseButton;
    ImageView decreaseButton;
    TextView Resdiario;
    TextView Resmensual;
    TextView Resanio;
    TextView ResTOTALAnio;
    TextView TituloTOTALAnio;
    Button BotonCalcularPension;
    Pensionado pensionado;
    NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance();
    final double UMA_2021 = 89.62;


    boolean mAutoIncrement = false;
    boolean mAutoDecrement = false;
    private Handler repeatUpdateHandler = new Handler();
    static int REP_DELAY = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado_mod40_expr);
        pensionado = (Pensionado) getIntent().getExtras().get("pensionado");
        TituloNombre = findViewById(R.id.textViewTituloNombre);
        String nombre = pensionado.getNombre() + " " + pensionado.getApellidos();
        TituloNombre.setText(nombre);
        BotonCalcularPension = findViewById(R.id.BotonCalcular_cexp);

        ImageView logoLM = findViewById(R.id.LogoLM);
        logoLM.setOnClickListener(v -> RegresarMenuPrin());

        tfieldSBC = findViewById(R.id.tfieldSBCaContratar);
        tfieldAnios = findViewById(R.id.tfieldaniosAcontratar);

        increaseButton = findViewById(R.id.imageViewIncrease);
        decreaseButton = findViewById(R.id.imageViewdecrease);
        Resdiario = findViewById(R.id.tvwDiarioResexp);
        Resmensual = findViewById(R.id.tvwMensualResexp);
        Resanio = findViewById(R.id.tvwAnualResexp);
        ResTOTALAnio = findViewById(R.id.tvwResAniosExpress);
        TituloTOTALAnio = findViewById(R.id.txyvwAniosExpress);

        tfieldSBC.getEditText().addTextChangedListener(validatenotempty);
        tfieldSBC.getEditText().setText("" +pensionado.getSBC());
        tfieldAnios.getEditText().addTextChangedListener(validatenotempty);

        // para que los botones incrementen el sbc
        increaseButton.setOnLongClickListener(
                new View.OnLongClickListener(){
                    public boolean onLongClick(View arg0) {
                        mAutoIncrement = true;
                        repeatUpdateHandler.post( new RptUpdater() );
                        return false;
                    }
                }
        );

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment();
            }
        });

        increaseButton.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()== MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement ){
                    mAutoIncrement = false;
                }
                return false;
            }
        });

        decreaseButton.setOnLongClickListener(
                new View.OnLongClickListener(){
                    public boolean onLongClick(View arg0) {
                        mAutoDecrement = true;
                        repeatUpdateHandler.post( new RptUpdater() );
                        return false;
                    }
                }
        );
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
            }
        });
        decreaseButton.setOnTouchListener( new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()== MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement ){
                    mAutoDecrement = false;
                }
                return false;
            }
        });


        backButton = findViewById(R.id.BackButtonCalculosRes);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        BotonCalcularPension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResultadoExpressMod40.this, CalculoPension2.class);
                // Hacer lo del promedio
                double anios = Double.parseDouble(tfieldAnios.getEditText().getText().toString());
                double sbc = Double.parseDouble(tfieldSBC.getEditText().getText().toString());

                pensionado.setSBC(PromediarSalarios(sbc,anios));
                intent.putExtra("pensionado", pensionado);
                intent.putExtra("historial",false);

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
            double sbc = 0;
            double anios = 0;
            boolean sihayanio = false;
            boolean paselejovenazo = false;

            if (!tfieldAnios.getEditText().getText().toString().isEmpty()){
                anios = Double.parseDouble(tfieldAnios.getEditText().getText().toString());
                if(anios <0 || anios>10){
                    tfieldAnios.setError("No puedes ingresar mas de 10 años");
                    sihayanio = false;
                }if (anios>5){
                    sihayanio = false;
                }
                else {
                    sihayanio = true;
                    tfieldAnios.setError(null);
                }
            }

            if(!tfieldSBC.getEditText().getText().toString().isEmpty()){
                sbc = Double.parseDouble(tfieldSBC.getEditText().getText().toString());
                if (sbc<pensionado.getSBC()){
                    tfieldSBC.setError("El S.B.C. a contratar no puede ser menor al ultimo obtenido");
                    paselejovenazo = false;
                }else{

                    if(sbc>(UMA_2021*25)){
                        tfieldSBC.setError("El maximo de sueldo permitido es 25 UMA, o (" + formatoMoneda.format((UMA_2021*25))+ ")");
                        paselejovenazo = false;
                    }else{
                        tfieldSBC.setError(null);
                        paselejovenazo = true;
                    }
                    double sbc_mod40_diario = sbc* 0.10075;
                    double sbc_mod40_mensual = sbc_mod40_diario*30.4;
                    double sbc_mod40_anual = sbc_mod40_diario*365;

                    Resdiario.setText(formatoMoneda.format(sbc_mod40_diario));
                    Resmensual.setText(formatoMoneda.format(sbc_mod40_mensual));
                    Resanio.setText(formatoMoneda.format(sbc_mod40_anual));
                    if (anios != 0){
                        double sbc_mod40_anualTOTAL = sbc_mod40_anual*anios;

                        if (anios == 1){
                            TituloTOTALAnio.setText("Total por un año:");
                        }else{
                            TituloTOTALAnio.setText("Total por " + anios + " años:");
                        }

                        TituloTOTALAnio.setVisibility(View.VISIBLE);
                        ResTOTALAnio.setVisibility(View.VISIBLE);
                        ResTOTALAnio.setText(formatoMoneda.format(sbc_mod40_anualTOTAL));
                    }else{
                        TituloTOTALAnio.setVisibility(View.GONE);
                        ResTOTALAnio.setVisibility(View.GONE);
                    }

                }
            }

            BotonCalcularPension.setEnabled( paselejovenazo && sihayanio);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    class RptUpdater implements Runnable {
        public void run() {
            if( mAutoIncrement ){
                increment();
                repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
            } else if( mAutoDecrement ){
                decrement();
                repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
            }
        }
    }

    public void increment(){
        String field = tfieldSBC.getEditText().getText().toString();
        double sbc = 0;
        if (field.isEmpty()){
            sbc = pensionado.getSBC();
        }else{
            sbc = Double.parseDouble(field);

            if (sbc<pensionado.getSBC()){
                sbc = pensionado.getSBC() + 100;
            }if (sbc >= UMA_2021*25){
                Toast.makeText(ResultadoExpressMod40.this, "No puedes ingresar mas de 25 UMA",Toast.LENGTH_SHORT).show();
            }
            else{
                sbc += 100;
            }
        }

        tfieldSBC.getEditText().setText( ""+sbc );
    }

    public void decrement(){
        String field = tfieldSBC.getEditText().getText().toString();
        double sbc = 0;
        if (field.isEmpty()){
            sbc = pensionado.getSBC();
            Toast.makeText(ResultadoExpressMod40.this, "No puedes ingresar menos del S.B.C. original",Toast.LENGTH_SHORT).show();
        }else{
            sbc = Double.parseDouble(field);
            if (sbc<=pensionado.getSBC()){
                sbc = pensionado.getSBC();
                Toast.makeText(ResultadoExpressMod40.this, "No puedes ingresar menos del S.B.C. original",Toast.LENGTH_SHORT).show();
            }else{
                sbc -= 100;
            }
        }

        tfieldSBC.getEditText().setText( ""+sbc );
    }

    public double PromediarSalarios(double SBC_M40,double anios){
        double anios_original = 5-anios;
        return (SBC_M40*anios + pensionado.getSBC()*anios_original)/5;
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

}


