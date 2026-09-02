package com.example.minicontrolrfid;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.minicontrolrfid.Activities.InventariosActivity;
import com.example.minicontrolrfid.Activities.LecturaRFIDActivity;
import com.example.minicontrolrfid.Activities.LoginActivity;
import com.example.minicontrolrfid.Activities.SeleccionarUbicacionActivity;
import com.example.minicontrolrfid.Activities.SincronizacionActivity;
import com.example.minicontrolrfid.UHF.UHFConfigActivity;
import com.example.minicontrolrfid.Util.Sesion;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener
{
    private TextView txtUsuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (!Sesion.HaySesionActiva(this))
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) ->
        {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtUsuarioActual = findViewById(R.id.txtUsuarioActual);
        txtUsuarioActual.setText(getString(R.string.login_Usuario) + " " + Sesion.ObtenerNombreActual(this));

        Button btnMenu = findViewById(R.id.btnMenu);
        btnMenu.setOnClickListener(this::ClickBtnMenuInicio);

        Button btnLecturaRFID = findViewById(R.id.btnLecturaRFID);
        btnLecturaRFID.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, LecturaRFIDActivity.class);
            startActivity(intent);
        });
    }

    public void ClickBtnMenuInicio(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_menuprincipal);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem)
    {
        int id = menuItem.getItemId();

        if (id == R.id.itemMenuIniciarInventario)
        {
            startActivity(new Intent(this, SeleccionarUbicacionActivity.class));
            return true;
        }
        else if (id == R.id.itemMenuConsultarInventarios)
        {
            startActivity(new Intent(this, InventariosActivity.class));
            return true;
        }
        else if (id == R.id.itemMenuSincronizar)
        {
            startActivity(new Intent(this, SincronizacionActivity.class));
            return true;
        }
        else if (id == R.id.itemMenuConfigRFID)
        {
            startActivity(new Intent(this, UHFConfigActivity.class));
            return true;
        }
        else if (id == R.id.itemMenuCerrarSesion)
        {
            Sesion.CerrarSesion(this);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return false;
    }
}
