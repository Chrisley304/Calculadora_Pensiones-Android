package com.chrisley.LM_calculadorpensiones;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    TextInputLayout email_tfield;
    TextInputLayout password_tfield;
    Button Login_Button;
    ImageButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        email_tfield = findViewById(R.id.email_logintextField);
        password_tfield = findViewById(R.id.password_logintextField);
        Login_Button = findViewById(R.id.BotonIngresar_login);
        cancelButton = findViewById(R.id.cancelButton);

        init();
    }

    private void init(){

        email_tfield.getEditText().addTextChangedListener(validatenotempty);
        password_tfield.getEditText().addTextChangedListener(validatenotempty);

        Login_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_tfield.getEditText().getText().toString().trim();
                String password = password_tfield.getEditText().getText().toString().trim();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            returnActivity(email,7);
                        }else{
                            Toast.makeText(LogInActivity.this,"Error al inciar sesión, verifica tus datos.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private TextWatcher validatenotempty = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String email = email_tfield.getEditText().getText().toString().trim();
            String password = password_tfield.getEditText().getText().toString().trim();

            Login_Button.setEnabled(!email.isEmpty() && !password.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void returnActivity(String email,int resultcode){
        Intent intent = new Intent();
        intent.putExtra("email",email);
        setResult(resultcode,intent);
        finish();//finishing activity
    }

}

