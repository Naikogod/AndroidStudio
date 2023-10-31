package com.example.orgalife;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class principal extends AppCompatActivity implements Grupo.OnTareaClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        // Fragmentos
        TabLayout tl = (TabLayout) findViewById(R.id.tablayout);
        tl.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Un toque
                int posicion = tab.getPosition();
                switch (posicion) {
                    case 0:
                        // Llamo al fragmento comunidad
                        Comunidad c = new Comunidad();
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, c).commit();
                        break;
                    case 1:
                        // Llamo al fragmento grupo
                        Grupo g = new Grupo();
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, g).commit();
                        break;
                    case 2:
                        // Llamo al fragmento completadas
                        Completadas co = new Completadas();
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, co).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Cuando no está tocado
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Cuando son más de un toque
                int posicion = tab.getPosition();
                switch (posicion) {
                    case 0:
                        // Llamo al fragmento comunidad
                        Comunidad c = new Comunidad();
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, c).commit();
                        break;
                    case 1:
                        // Llamo al fragmento grupo
                        Grupo g = new Grupo();
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, g).commit();
                        break;
                    case 2:
                        // Llamo al fragmento completadas
                        Completadas co = new Completadas();
                        getSupportFragmentManager().beginTransaction().replace(R.id.contenedor_principal, co).commit();
                        break;
                }
            }
        });

        // Menu Lateral
        NavigationView nav = (NavigationView) findViewById(R.id.nav);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Recuperar la opción de menú lateral
                int id = item.getItemId(); // Recuperar id de la opción
                if (id == R.id.op1) {
                    Intent i = new Intent(principal.this, Cuenta.class);
                    startActivity(i);
                } else if (id == R.id.op3) {
                    // Abrir los "Términos de uso de Google" en un navegador web
                    String url = "https://policies.google.com/terms";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else if (id == R.id.op4) {
                    // Cerrar la sesión de Firebase
                    FirebaseAuth.getInstance().signOut();
                    // Borrar la información de recordar usuario en SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("MisPreferencias", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("correo");
                    editor.remove("contrasenia");
                    editor.remove("recordarme");
                    editor.apply();
                    finish(); // Cierra la actividad actual
                }
                return false;
            }
        });

        // Configuración del menú lateral Cuando está escondido, etc.
        DrawerLayout dl = (DrawerLayout) findViewById(R.id.drawer_principal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                dl,
                R.string.Abrir_Drawer,
                R.string.Cerrar_Drawer
        );

        dl.addDrawerListener(toggle);
        toggle.syncState();
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dl.isDrawerOpen(GravityCompat.START)) {
                    dl.closeDrawer(GravityCompat.START);
                } else {
                    dl.openDrawer(GravityCompat.START);
                }
            }
        });

        // Recuperar el usuario actual
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            // Referencia a Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Obtener la imagen de perfil y el nombre del usuario desde Firestore
            db.collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Recuperar la URL de la imagen de perfil y el nombre del usuario
                                String imageUrl = document.getString("imageUrl");
                                String email = user.getEmail();
                                String username = email.substring(0, email.indexOf("@"));

                                // Establecer la imagen y el nombre en el header
                                ImageView headerImageView = nav.getHeaderView(0).findViewById(R.id.headerImageView);
                                TextView headerTextView = nav.getHeaderView(0).findViewById(R.id.headerTextView);

                                // Glide es una biblioteca de carga de imágenes que puedes usar para cargar la imagen
                                // Puedes agregar la dependencia en tu archivo build.gradle
                                // implementation 'com.github.bumptech.glide:glide:4.12.0'
                                // y luego cargar la imagen con Glide
                                Glide.with(this)
                                        .load(imageUrl)
                                        .into(headerImageView);

                                headerTextView.setText(username);
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return true;
    }

    // Implementa el método onTareaClicked para manejar el clic en una tarea
    @Override
    public void onTareaClicked(Tarea tarea) {
        // Reemplazar el fragmento Grupo por el fragmento Comentarios
        Comentarios comentariosFragment = new Comentarios();

        // Configura los argumentos con el nombre de la tarea
        Bundle args = new Bundle();
        args.putString("nombreTarea", tarea.getNombre());
        comentariosFragment.setArguments(args);

        // Iniciar una transacción de fragmento
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_principal, comentariosFragment)
                .addToBackStack(null) // Opcional: permite volver al fragmento anterior con el botón Atrás
                .commit();
    }
}


