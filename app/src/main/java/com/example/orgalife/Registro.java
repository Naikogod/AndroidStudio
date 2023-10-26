package com.example.orgalife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Registro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
    }

    public void Registrar(View v) {
        // Traer los valores y pasarlos a una cadena
        EditText campo1 = this.findViewById(R.id.correo);
        String getCorreo = campo1.getText().toString();
        EditText campo2 = this.findViewById(R.id.contrasenia);
        String getContrasenia = campo2.getText().toString();

        // Realizar validación de campos
        if (!isValidEmail(getCorreo)) {
            campo1.setError("Correo electrónico no válido");
            return;
        }

        if (getContrasenia.length() < 6) {
            campo2.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        // Registrar al usuario en Firebase
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(getCorreo, getContrasenia)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // El usuario se ha registrado con éxito
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Puedes realizar otras acciones aquí, como guardar datos del usuario en Firestore.
                            Toast.makeText(Registro.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(Registro.this, Iniciar_Sesion.class);
                            startActivity(i);
                        } else {
                            // Manejar errores durante el registro
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(Registro.this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Registro.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }
}
