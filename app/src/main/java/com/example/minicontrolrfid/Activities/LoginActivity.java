package com.example.minicontrolrfid.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.minicontrolrfid.DB.Api;
import com.example.minicontrolrfid.DB.DBLocal;
import com.example.minicontrolrfid.Entidades.Usuarios;
import com.example.minicontrolrfid.MainActivity;
import com.example.minicontrolrfid.R;
import com.example.minicontrolrfid.Util.Seguridad;
import com.example.minicontrolrfid.Util.Sesion;

public class LoginActivity extends AppCompatActivity {

    private Api api;
    private DBLocal dbLocal;
    private EditText txtUsuario;
    private EditText txtPassword;
    private Button btnIniciarSesion;
    private TextView lblMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);

        api = new Api(this);
        dbLocal = new DBLocal(this);

        txtUsuario = findViewById(R.id.txtUsuario);
        txtPassword = findViewById(R.id.txtPassword);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        lblMensaje = findViewById(R.id.lblMensaje);

        btnIniciarSesion.setOnClickListener(v -> IniciarSesion());
    }

    private void IniciarSesion() {
        String usuario = txtUsuario.getText().toString().trim();
        String password = txtPassword.getText().toString();

        if (usuario.isEmpty() || password.isEmpty()) {
            MostrarError(getString(R.string.login_ErrorIniciarSesion));
            return;
        }

        btnIniciarSesion.setEnabled(false);
        lblMensaje.setVisibility(View.GONE);

        new Thread(() -> {
            String passwordHasheado = Seguridad.encriptarCadenaSHA256(password);

            // Primero se intenta validar contra el servidor; si no hay conexion,
            // se valida contra el catalogo local sincronizado en una sesion anterior
            Usuarios usuarioValidado = api.Login(usuario, passwordHasheado);

            if (usuarioValidado == null) {
                usuarioValidado = dbLocal.ValidarLogin(usuario, passwordHasheado);
            }

            Usuarios usuarioFinal = usuarioValidado;

            runOnUiThread(() -> {
                btnIniciarSesion.setEnabled(true);

                if (usuarioFinal != null) {
                    Sesion.GuardarSesion(LoginActivity.this, usuarioFinal);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    MostrarError(getString(R.string.login_ErrorIniciarSesion));
                }
            });
        }).start();
    }

    private void MostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setVisibility(View.VISIBLE);
    }
}
