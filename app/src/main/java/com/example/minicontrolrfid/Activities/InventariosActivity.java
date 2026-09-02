package com.example.minicontrolrfid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minicontrolrfid.Adapters.InventarioAdapter;
import com.example.minicontrolrfid.DB.DBLocal;
import com.example.minicontrolrfid.Entidades.Inventario;
import com.example.minicontrolrfid.Entidades.Ubicacion;
import com.example.minicontrolrfid.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventariosActivity extends AppCompatActivity {

    public static final String EXTRA_ID_INVENTARIO = "IdInventario";

    private DBLocal dbLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inventarios);

        dbLocal = new DBLocal(this);

        ListView lvInventarios = findViewById(R.id.lvInventarios);
        findViewById(R.id.btnRegresarInventarios).setOnClickListener(v -> finish());

        ArrayList<Inventario> inventarios = dbLocal.ObtenerInventarios();
        if (inventarios == null) {
            inventarios = new ArrayList<>();
        }

        Map<Integer, String> ubicaciones = new HashMap<>();
        ArrayList<Ubicacion> todasUbicaciones = dbLocal.ObtenerUbicaciones();
        if (todasUbicaciones != null) {
            for (Ubicacion ubicacion : todasUbicaciones) {
                ubicaciones.put(ubicacion.getIdUbicacion(), ubicacion.getDescripcion());
            }
        }

        InventarioAdapter adapter = new InventarioAdapter(this, inventarios, ubicaciones);
        lvInventarios.setAdapter(adapter);

        ArrayList<Inventario> listaFinal = inventarios;
        lvInventarios.setOnItemClickListener((parent, view, position, id) -> {
            Inventario seleccionado = listaFinal.get(position);

            Intent intent = new Intent(this, InventarioDetalleActivity.class);
            intent.putExtra(EXTRA_ID_INVENTARIO, seleccionado.getIdInventario());
            startActivity(intent);
        });
    }
}
