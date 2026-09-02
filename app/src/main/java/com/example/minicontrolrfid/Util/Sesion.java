package com.example.minicontrolrfid.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.minicontrolrfid.Entidades.Usuarios;

public class Sesion {

    // Mismo archivo de preferencias que ya usa UHFConfigActivity (RSSI, ModoOffLineActivado)
    private static final String PREFERENCIAS = "PreferenciasApp";

    private static final String CLAVE_ID_USUARIO = "Sesion_IdUsuario";
    private static final String CLAVE_USUARIO = "Sesion_Usuario";
    private static final String CLAVE_NOMBRE = "Sesion_Nombre";
    private static final String CLAVE_ID_UBICACION = "Sesion_IdUbicacionSeleccionada";

    public static void GuardarSesion(Context context, Usuarios usuario) {
        obtenerPreferencias(context).edit()
                .putInt(CLAVE_ID_USUARIO, usuario.getIdUsuario())
                .putString(CLAVE_USUARIO, usuario.getUsuario())
                .putString(CLAVE_NOMBRE, usuario.getNombre())
                .apply();
    }

    public static boolean HaySesionActiva(Context context) {
        return obtenerPreferencias(context).contains(CLAVE_ID_USUARIO);
    }

    public static int ObtenerIdUsuarioActual(Context context) {
        return obtenerPreferencias(context).getInt(CLAVE_ID_USUARIO, 0);
    }

    public static String ObtenerUsuarioActual(Context context) {
        return obtenerPreferencias(context).getString(CLAVE_USUARIO, "");
    }

    public static String ObtenerNombreActual(Context context) {
        return obtenerPreferencias(context).getString(CLAVE_NOMBRE, "");
    }

    public static void CerrarSesion(Context context) {
        // Solo se borran las claves de sesion; el resto de PreferenciasApp (config RFID) no se toca
        obtenerPreferencias(context).edit()
                .remove(CLAVE_ID_USUARIO)
                .remove(CLAVE_USUARIO)
                .remove(CLAVE_NOMBRE)
                .apply();
    }

    public static void GuardarUbicacionSeleccionada(Context context, int idUbicacion) {
        obtenerPreferencias(context).edit()
                .putInt(CLAVE_ID_UBICACION, idUbicacion)
                .apply();
    }

    public static int ObtenerUbicacionSeleccionada(Context context) {
        return obtenerPreferencias(context).getInt(CLAVE_ID_UBICACION, 0);
    }

    private static SharedPreferences obtenerPreferencias(Context context) {
        return context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
    }
}
