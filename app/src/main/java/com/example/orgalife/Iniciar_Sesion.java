package com.example.orgalife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import androidx.annotation.NonNull;

public class Iniciar_Sesion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);
    }

    public void Validar(View v) {
        // Obtener los valores ingresados por el usuario
        EditText campoCorreoInicioSesion = findViewById(R.id.correo2);
        String correoIngresado = campoCorreoInicioSesion.getText().toString();
        EditText campoContraseniaInicioSesion = findViewById(R.id.contrasenia2);
        String contraseniaIngresada = campoContraseniaInicioSesion.getText().toString();

        // Recuperar el estado del CheckBox "Recordarme"
        CheckBox chRecuerdame = findViewById(R.id.Recuerdame);
        boolean Chequeado = chRecuerdame.isChecked();

        if (TextUtils.isEmpty(correoIngresado) || TextUtils.isEmpty(contraseniaIngresada)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (contraseniaIngresada.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        iniciarSesion(correoIngresado, contraseniaIngresada, Chequeado);
    }

    private void iniciarSesion(String correo, String contrasenia, boolean recordarme) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        mAuth.signInWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (recordarme) {
                                guardarDatosEnSharedPreferences(correo, contrasenia);
                            }
                            Toast.makeText(Iniciar_Sesion.this, "Inicio Exitoso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Iniciar_Sesion.this, principal.class);
                            startActivity(intent);
                            finish(); // Cierra la actividad actual
                        } else {
                            Toast.makeText(Iniciar_Sesion.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void guardarDatosEnSharedPreferences(String correo, String contrasenia) {
        SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("correo", correo);
        editor.putString("contrasenia", contrasenia);
        editor.putBoolean("recordarme", true);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
        boolean recordarme = sharedPreferences.getBoolean("recordarme", false);
        CheckBox chRecuerdame = findViewById(R.id.Recuerdame);
        chRecuerdame.setChecked(recordarme);

        if (recordarme) {
            String correoRegistrado = sharedPreferences.getString("correo", "");
            String contraseniaRegistrada = sharedPreferences.getString("contrasenia", "");
            iniciarSesion(correoRegistrado, contraseniaRegistrada, true);
        }
    }
}

