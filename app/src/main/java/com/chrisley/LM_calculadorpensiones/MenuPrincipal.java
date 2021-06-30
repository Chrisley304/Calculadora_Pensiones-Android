package com.chrisley.LM_calculadorpensiones;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuPrincipal extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final int EMAIL_LOGIN = 1;
    private final int RESULT_OK = 7;
    private final int RESULT_CANCEL = 9;
    String nombreUsuario = "";
    String apellidoUsuario = "";
    String emailUser = "";
    Button CalcExpress;
    Button Mod40;
    ImageView Historial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CalcExpress = findViewById(R.id.Botonexpress_home);
        Mod40 = findViewById(R.id.BotonMod40home);
        Historial = findViewById(R.id.BotonHistorial);

        CalcExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CambiodeScreen(1);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(MenuPrincipal.this, "Error al cambiar de pantalla", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Mod40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CambiodeScreen(2);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(MenuPrincipal.this, "Error al cambiar de pantalla", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Historial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CambiodeScreen(3);
                } catch (IllegalArgumentException e) {
                    Toast.makeText(MenuPrincipal.this, "Error al cambiar de pantalla", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void CambiodeScreen(int screen) throws IllegalArgumentException {
        switch (screen) {

            case 1: {
                Intent intent = new Intent(this, CalculoExpressAc.class);
                intent.putExtra("email",emailUser);
                startActivity(intent);
            }
            break;

            case 2: {
                Intent intent = new Intent(this, Mod40Express1.class);
                intent.putExtra("email",emailUser);
                startActivity(intent);
            }
            break;

            case 3: {
                if (isLogged()){
                    Intent intentHistorial = new Intent(this, HIstorialActivity.class);
                    intentHistorial.putExtra("nombre",nombreUsuario);
                    intentHistorial.putExtra("apellido",apellidoUsuario);
                    intentHistorial.putExtra("email", emailUser);
                    startActivity(intentHistorial);
                }else{
                    Intent intent = new Intent(MenuPrincipal.this,LogInActivity.class);
                    startActivityForResult(intent,EMAIL_LOGIN);
                    Animatoo.animateSlideUp(this);
                }

            }
            break;

            default: {
                throw new IllegalArgumentException();
            }

        }
    }

    private boolean isLogged(){
        SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
        emailUser = prefs.getString("email",null);
        nombreUsuario = prefs.getString("nombre",null);
        apellidoUsuario = prefs.getString("apellido",null);
        return emailUser != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == EMAIL_LOGIN && resultCode == RESULT_OK){
            emailUser = data.getStringExtra("email");
            SharedPreferences prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE);
            prefs.edit().putString("email",emailUser).apply();
            db.collection("nombre_usuarios").document(emailUser).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    nombreUsuario = String.valueOf(documentSnapshot.get("nombre"));
                    apellidoUsuario = String.valueOf(documentSnapshot.get("apellido"));
                    prefs.edit().putString("nombre",nombreUsuario).apply();
                    prefs.edit().putString("apellido",apellidoUsuario).apply();
                    Toast.makeText(MenuPrincipal.this,"Bienvenido " + nombreUsuario,Toast.LENGTH_SHORT).show();
                }
            });


        }
    }
}