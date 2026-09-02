package com.example.minicontrolrfid.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minicontrolrfid.DB.Api;
import com.example.minicontrolrfid.DB.DBLocal;
import com.example.minicontrolrfid.Entidades.Inventario;
import com.example.minicontrolrfid.R;

import java.util.ArrayList;

public class SincronizacionActivity extends AppCompatActivity {

    private Api api;
    private DBLocal dbLocal;
    private TextView txtTotalPorAuditar;
    private ProgressBar progressBarSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sync);

        api = new Api(this);
        dbLocal = new DBLocal(this);

        txtTotalPorAuditar = findViewById(R.id.txtTotalPorAuditar);
        progressBarSync = findViewById(R.id.progressBarSync);

        Button btnSincronizarCatalogos = findViewById(R.id.btnSincronizarCatalogos);
        Button btnSubirPendientes = findViewById(R.id.btnSubirPendientes);
        Button btnRegresarSync = findViewById(R.id.btnRegresarSync);

        btnSincronizarCatalogos.setOnClickListener(v -> SincronizarCatalogos());
        btnSubirPendientes.setOnClickListener(v -> SubirPendientes());
        btnRegresarSync.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActualizarTotalPendientes();
    }

    private void ActualizarTotalPendientes() {
        ArrayList<Inventario> pendientes = dbLocal.ObtenerInventariosPendientes();
        int total = (pendientes != null) ? pendientes.size() : 0;

        if (total == 0) {
            txtTotalPorAuditar.setText(getString(R.string.txt_NoTieneActivosParaSync));
        } else {
            txtTotalPorAuditar.setText(total + " " + getString(R.string.txt_FilasEnLocal));
        }
    }

    private void SincronizarCatalogos() {
        progressBarSync.setVisibility(View.VISIBLE);

        api.SincronizarCatalogos(dbLocal, () -> runOnUiThread(() -> {
            progressBarSync.setVisibility(View.GONE);
            MostrarResultado();
        }));
    }

    private void SubirPendientes() {
        ArrayList<Inventario> pendientes = dbLocal.ObtenerInventariosPendientes();

        if (pendientes == null || pendientes.isEmpty()) {
            Toast.makeText(this, getString(R.string.txt_NoTieneActivosParaSync), Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarSync.setVisibility(View.VISIBLE);

        api.EnviarInventariosPendientes(pendientes, dbLocal, () -> runOnUiThread(() -> {
            progressBarSync.setVisibility(View.GONE);
            ActualizarTotalPendientes();
            MostrarResultado();
        }));
    }

    private void MostrarResultado() {
        if (api.Error == null || api.Error.isEmpty()) {
            Toast.makeText(this, getString(R.string.txt_SincronizacionCompleta), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, api.Error, Toast.LENGTH_LONG).show();
        }
    }
}
