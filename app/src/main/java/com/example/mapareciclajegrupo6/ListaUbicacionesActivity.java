package com.example.mapareciclajegrupo6;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ListaUbicacionesActivity extends AppCompatActivity {

    ListView listaUbicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_ubicaciones);

        listaUbicaciones = findViewById(R.id.listaUbicaciones);

        SharedPreferences prefs = getSharedPreferences("MisUbicaciones", MODE_PRIVATE);
        Set<String> ubicaciones = prefs.getStringSet("ubicaciones_guardadas", new HashSet<>());

        ArrayList<String> lista = new ArrayList<>(ubicaciones);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                lista
        );

        listaUbicaciones.setAdapter(adapter);
    }
}
