package com.example.orgalife;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void vRegistrar(View v){
        Intent i = new Intent(this, Registro.class);
        startActivity(i);
    }
    public void vIniciar(View v){
        Intent i = new Intent(this, Iniciar_Sesion.class);
        startActivity(i);
    }
}