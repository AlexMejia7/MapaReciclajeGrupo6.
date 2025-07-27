package com.example.mapareciclajegrupo6;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapActivity extends AppCompatActivity {

    private MapView map;

    private static final String PREFS_NAME = "MisUbicaciones";
    private static final String KEY_UBICACIONES = "ubicaciones_guardadas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_map);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(15.0);
        map.getController().setCenter(new GeoPoint(14.0818, -87.2068)); // Centro en Tegucigalpa

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mostrarUbicacion();
        }

        List<GeoPoint> puntos = new ArrayList<>();
        puntos.add(new GeoPoint(14.0818, -87.2068)); // Punto 1
        puntos.add(new GeoPoint(14.0830, -87.2050)); // Punto 2
        puntos.add(new GeoPoint(14.0800, -87.2080)); // Punto 3

        Drawable icon = getResources().getDrawable(R.drawable.marker_icon, null);
        Bitmap original = ((BitmapDrawable) icon).getBitmap();
        Bitmap scaled = Bitmap.createScaledBitmap(original, 80, 80, false);
        Drawable scaledDrawable = new BitmapDrawable(getResources(), scaled);

        for (GeoPoint punto : puntos) {
            Marker marker = new Marker(map);
            marker.setPosition(punto);
            marker.setTitle("Punto ecológico");
            marker.setSubDescription("Haz clic para más información");
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setIcon(scaledDrawable);
            marker.setOnMarkerClickListener((marker1, mapView) -> {
                Toast.makeText(this, "Ubicación: " + marker1.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            });
            map.getOverlays().add(marker);
        }

        Polyline ruta = new Polyline();
        ruta.setWidth(5f);
        ruta.setColor(Color.BLUE);
        ruta.setPoints(puntos);
        map.getOverlays().add(ruta);

        // Botón Guardar ubicación actual (centro del mapa)
        Button btnGuardar = findViewById(R.id.btnGuardarUbicacion);
        btnGuardar.setOnClickListener(view -> {
            GeoPoint centro = (GeoPoint) map.getMapCenter();
            double lat = centro.getLatitude();
            double lon = centro.getLongitude();

            guardarUbicacion(lat, lon);

            Toast.makeText(MapActivity.this, "Ubicación guardada:\nLat: " + lat + "\nLon: " + lon, Toast.LENGTH_SHORT).show();

            Marker marker = new Marker(map);
            marker.setPosition(centro);
            marker.setTitle("Ubicación guardada");
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            map.getOverlays().add(marker);
            map.invalidate();
        });

        // Botón Ver Ubicaciones
        Button btnVer = findViewById(R.id.btnVerUbicaciones);
        btnVer.setOnClickListener(v -> {
            Intent intent = new Intent(MapActivity.this, ListaUbicacionesActivity.class);
            startActivity(intent);
        });
    }

    private void guardarUbicacion(double lat, double lon) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> ubicaciones = prefs.getStringSet(KEY_UBICACIONES, new HashSet<>());

        // Guardar como string "lat,lon"
        String nuevaUbicacion = lat + "," + lon;

        // Para evitar modificar el Set original que es inmutable, crear uno nuevo
        Set<String> nuevaLista = new HashSet<>(ubicaciones);
        nuevaLista.add(nuevaUbicacion);

        prefs.edit().putStringSet(KEY_UBICACIONES, nuevaLista).apply();
    }

    private void mostrarUbicacion() {
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
}
