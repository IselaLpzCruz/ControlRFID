package com.example.minicontrolrfid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minicontrolrfid.Adapters.UbicacionAdapter;
import com.example.minicontrolrfid.DB.DBLocal;
import com.example.minicontrolrfid.Entidades.Ubicacion;
import com.example.minicontrolrfid.R;
import com.example.minicontrolrfid.Util.Sesion;

import java.util.ArrayList;

public class SeleccionarUbicacionActivity extends AppCompatActivity {

    private DBLocal dbLocal;
    private ListView lvUbicaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_seleccionar_ubicacion);

        dbLocal = new DBLocal(this);
        lvUbicaciones = findViewById(R.id.lvUbicaciones);

        findViewById(R.id.btnRegresarUbicacion).setOnClickListener(v -> finish());

        CargarUbicacionesActivas();
    }

    private void CargarUbicacionesActivas() {
        ArrayList<Ubicacion> todas = dbLocal.ObtenerUbicaciones();
        ArrayList<Ubicacion> activas = new ArrayList<>();

        if (todas != null) {
            for (Ubicacion ubicacion : todas) {
                if (ubicacion.getActivo()) {
                    activas.add(ubicacion);
                }
            }
        }

        if (activas.isEmpty()) {
            Toast.makeText(this, "No hay ubicaciones, sincronice primero.", Toast.LENGTH_SHORT).show();
        }

        UbicacionAdapter adapter = new UbicacionAdapter(this, activas);
        lvUbicaciones.setAdapter(adapter);

        lvUbicaciones.setOnItemClickListener((parent, view, position, id) -> {
            Ubicacion seleccionada = activas.get(position);
            Sesion.GuardarUbicacionSeleccionada(this, seleccionada.getIdUbicacion());

            startActivity(new Intent(this, InventarioActivity.class));
            finish();
        });
    }
}
