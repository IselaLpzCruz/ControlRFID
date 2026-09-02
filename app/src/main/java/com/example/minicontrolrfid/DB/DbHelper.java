package com.example.minicontrolrfid.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NOMBRE = "minicontrolrfid.db";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
    }

    public void EliminarDB(Context context) {
        context.deleteDatabase(DATABASE_NOMBRE);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CrearTablas(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        EliminarTablas(sqLiteDatabase);
        CrearTablas(sqLiteDatabase);
    }

    private void EliminarTablas(SQLiteDatabase sqLiteDatabase) {
        try {sqLiteDatabase.execSQL("DROP TABLE Usuarios");}catch (Exception e){ }
        try {sqLiteDatabase.execSQL("DROP TABLE Ubicaciones");}catch (Exception e){ }
        try {sqLiteDatabase.execSQL("DROP TABLE Activos");}catch (Exception e){ }
        try {sqLiteDatabase.execSQL("DROP TABLE Inventarios");}catch (Exception e){ }
        try {sqLiteDatabase.execSQL("DROP TABLE InventarioDetalle");}catch (Exception e){ }
    }

    private void CrearTablas(SQLiteDatabase sqLiteDatabase) {

        CrearTabla(sqLiteDatabase, "CREATE TABLE Usuarios( " +
                "IdUsuario INTEGER PRIMARY KEY, " +
                "Nombre varchar(150) NOT NULL, " +
                "Usuario varchar(20) NOT NULL, " +
                "Password varchar(200) NOT NULL, " +
                "Activo bit NOT NULL, " +
                "FechaAlta datetime NOT NULL, " +
                "FechaMod datetime NULL, " +
                "UsuarioAlta varchar(20) NOT NULL, " +
                "UsuarioMod varchar(20) NULL" +
                ")");

        CrearTabla(sqLiteDatabase, "CREATE TABLE Ubicaciones( " +
                "IdUbicacion INTEGER PRIMARY KEY, " +
                "Clave varchar(50) NOT NULL, " +
                "Descripcion varchar(200) NOT NULL, " +
                "Activo bit NOT NULL, " +
                "FechaAlta datetime NOT NULL, " +
                "FechaMod datetime NULL, " +
                "UsuarioAlta varchar(20) NOT NULL, " +
                "UsuarioMod varchar(20) NULL" +
                ")");

        CrearTabla(sqLiteDatabase, "CREATE TABLE Activos( " +
                "IdActivo INTEGER PRIMARY KEY, " +
                "CodigoActivo varchar(50) NOT NULL, " +
                "Descripcion varchar(250) NOT NULL, " +
                "EPC varchar(100) NULL, " +
                "Barcode varchar(100) NULL, " +
                "IdUbicacion int NULL, " +
                "Marca varchar(100) NULL, " +
                "Modelo varchar(100) NULL, " +
                "NumeroSerie varchar(100) NULL, " +
                "Activo bit NOT NULL, " +
                "FechaAlta datetime NOT NULL, " +
                "FechaMod datetime NULL, " +
                "UsuarioAlta varchar(20) NOT NULL, " +
                "UsuarioMod varchar(20) NULL" +
                ")");

        CrearTabla(sqLiteDatabase, "CREATE TABLE Inventarios( " +
                "IdInventario INTEGER PRIMARY KEY, " +
                "FechaInventario datetime NOT NULL, " +
                "IdUsuario int NOT NULL, " +
                "IdUbicacion int NULL, " +
                "Estatus varchar(20) NOT NULL, " +
                "Sincronizado bit NOT NULL, " +
                "Activo bit NOT NULL, " +
                "FechaAlta datetime NOT NULL, " +
                "FechaMod datetime NULL, " +
                "UsuarioAlta varchar(20) NOT NULL, " +
                "UsuarioMod varchar(20) NULL" +
                ")");

        CrearTabla(sqLiteDatabase, "CREATE TABLE InventarioDetalle( " +
                "IdInventarioDetalle INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "IdInventario int NOT NULL, " +
                "IdActivo int NULL, " +
                "EPC varchar(100) NULL, " +
                "Barcode varchar(100) NULL, " +
                "TipoLectura varchar(20) NOT NULL, " +
                "FechaLectura datetime NOT NULL, " +
                "Cantidad int NOT NULL, " +
                "Sincronizado bit NOT NULL, " +
                "Activo bit NOT NULL, " +
                "FechaAlta datetime NOT NULL, " +
                "FechaMod datetime NULL, " +
                "UsuarioAlta varchar(20) NOT NULL, " +
                "UsuarioMod varchar(20) NULL" +
                ")");

        CrearTabla(sqLiteDatabase,
                "CREATE UNIQUE INDEX idx_inventariodetalle_unico " +
                        "ON InventarioDetalle(IdInventario, IdActivo)");
    }

    private void CrearTabla(SQLiteDatabase sqLiteDatabase, String sql) {
        sqLiteDatabase.execSQL(sql);
    }

}
