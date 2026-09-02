package com.example.minicontrolrfid.Activities;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minicontrolrfid.Adapters.InventarioDetalleAdapter;
import com.example.minicontrolrfid.DB.DBLocal;
import com.example.minicontrolrfid.Entidades.Activo;
import com.example.minicontrolrfid.Entidades.Inventario;
import com.example.minicontrolrfid.Entidades.InventarioDetalle;
import com.example.minicontrolrfid.Entidades.Ubicacion;
import com.example.minicontrolrfid.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InventarioDetalleActivity extends AppCompatActivity {

    private DBLocal dbLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inventario_detalle);

        dbLocal = new DBLocal(this);

        int idInventario = getIntent().getIntExtra(InventariosActivity.EXTRA_ID_INVENTARIO, 0);

        TextView txtEncabezado = findViewById(R.id.txtEncabezadoDetalle);
        ListView lvDetalle = findViewById(R.id.lvDetalleInventario);
        findViewById(R.id.btnRegresarDetalle).setOnClickListener(v -> finish());

        Inventario inventario = dbLocal.ObtenerInventarioPorId(idInventario);
        txtEncabezado.setText(ObtenerTextoEncabezado(inventario));

        ArrayList<InventarioDetalle> detalle = dbLocal.ObtenerDetalleInventario(idInventario);
        if (detalle == null) {
            detalle = new ArrayList<>();
        }

        Map<Integer, Activo> activos = new HashMap<>();
        for (InventarioDetalle d : detalle) {
            if (d.getIdActivo() != null && !activos.containsKey(d.getIdActivo())) {
                Activo activo = dbLocal.ObtenerActivoPorId(d.getIdActivo());
                if (activo != null) {
                    activos.put(d.getIdActivo(), activo);
                }
            }
        }

        InventarioDetalleAdapter adapter = new InventarioDetalleAdapter(this, detalle, activos);
        lvDetalle.setAdapter(adapter);
    }

    private String ObtenerTextoEncabezado(Inventario inventario) {
        if (inventario == null) {
            return "";
        }

        Ubicacion ubicacion = (inventario.getIdUbicacion() != null)
                ? dbLocal.ObtenerUbicacionPorId(inventario.getIdUbicacion())
                : null;

        String fecha = inventario.getFechaInventario() != null
                ? new SimpleDateFormat("dd/MM/yyyy").format(inventario.getFechaInventario())
                : "";

        return fecha + " - " + (ubicacion != null ? ubicacion.getDescripcion() : "")
                + " - " + inventario.getEstatus();
    }
}
