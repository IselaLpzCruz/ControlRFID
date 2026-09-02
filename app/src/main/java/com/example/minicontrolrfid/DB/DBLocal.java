package com.example.minicontrolrfid.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.minicontrolrfid.Entidades.Activo;
import com.example.minicontrolrfid.Entidades.Inventario;
import com.example.minicontrolrfid.Entidades.InventarioDetalle;
import com.example.minicontrolrfid.Entidades.Ubicacion;
import com.example.minicontrolrfid.Entidades.Usuarios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBLocal extends DbHelper {

    SQLiteDatabase dbWriter;
    SQLiteDatabase dbReader;

    public String Error = "";

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DBLocal(@Nullable Context context) {
        super(context);
        DbHelper dbHelper = new DbHelper(context);
        dbWriter = dbHelper.getWritableDatabase();
        dbReader = dbHelper.getReadableDatabase();
    }

    private String FormatearFecha(Date fecha) {
        return fecha != null ? FORMATO_FECHA.format(fecha) : null;
    }

    private Date ParsearFecha(String fecha) {
        try {
            return (fecha != null && !fecha.isEmpty()) ? FORMATO_FECHA.parse(fecha) : null;
        } catch (ParseException e) {
            return null;
        }
    }

    /* =========================== USUARIOS =========================== */

    public boolean InsertarUsuario(Usuarios usuario) {
        boolean r = false;
        try {
            ContentValues values = new ContentValues();
            values.put("IdUsuario", usuario.getIdUsuario());
            values.put("Nombre", usuario.getNombre());
            values.put("Usuario", usuario.getUsuario());
            values.put("Password", usuario.getPassword());
            values.put("Activo", usuario.getActivo() ? 1 : 0);
            values.put("FechaAlta", FormatearFecha(usuario.getFechaAlta()));
            values.put("FechaMod", FormatearFecha(usuario.getFechaMod()));
            values.put("UsuarioAlta", usuario.getUsuarioAlta());
            values.put("UsuarioMod", usuario.getUsuarioMod());
            r = dbWriter.insert("Usuarios", null, values) != -1;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    public Usuarios ObtenerUsuario(int idUsuario) {
        Usuarios dato = null;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Usuarios WHERE IdUsuario=? AND Usuario = ?",
                    new String[]{String.valueOf(idUsuario)});
            if (cursor.moveToFirst()) {
                dato = LeerUsuario(cursor);
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return dato;
    }

    public Usuarios ValidarLogin(String usuario, String passwordHasheado) {
        Usuarios dato = null;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery(
                    "SELECT * FROM Usuarios WHERE Usuario=? AND Password=? AND Activo=1",
                    new String[]{usuario, passwordHasheado});
            if (cursor.moveToFirst()) {
                dato = LeerUsuario(cursor);
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return dato;
    }

    public boolean LimpiarUsuarios() {
        boolean r = false;
        try {
            dbWriter.execSQL("DELETE FROM Usuarios");
            r = true;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    private Usuarios LeerUsuario(Cursor cursor) {
        Usuarios dato = new Usuarios();
        dato.setIdUsuario(cursor.getInt(0));
        dato.setNombre(cursor.getString(1));
        dato.setUsuario(cursor.getString(2));
        dato.setPassword(cursor.getString(3));
        dato.setActivo(cursor.getInt(4) == 1);
        dato.setFechaAlta(ParsearFecha(cursor.getString(5)));
        dato.setFechaMod(ParsearFecha(cursor.getString(6)));
        dato.setUsuarioAlta(cursor.getString(7));
        dato.setUsuarioMod(cursor.getString(8));
        return dato;
    }

    /* =========================== UBICACIONES =========================== */

    public boolean InsertarUbicacion(Ubicacion ubicacion) {
        boolean r = false;
        try {
            ContentValues values = new ContentValues();
            values.put("IdUbicacion", ubicacion.getIdUbicacion());
            values.put("Clave", ubicacion.getClave());
            values.put("Descripcion", ubicacion.getDescripcion());
            values.put("Activo", ubicacion.getActivo() ? 1 : 0);
            values.put("FechaAlta", FormatearFecha(ubicacion.getFechaAlta()));
            values.put("FechaMod", FormatearFecha(ubicacion.getFechaMod()));
            values.put("UsuarioAlta", ubicacion.getUsuarioAlta());
            values.put("UsuarioMod", ubicacion.getUsuarioMod());
            r = dbWriter.insert("Ubicaciones", null, values) != -1;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    public ArrayList<Ubicacion> ObtenerUbicaciones() {
        ArrayList<Ubicacion> datos = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Ubicaciones", null);
            if (cursor.moveToFirst()) {
                do {
                    datos.add(LeerUbicacion(cursor));
                } while (cursor.moveToNext());
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return datos;
    }

    public Ubicacion ObtenerUbicacionPorId(int idUbicacion) {
        Ubicacion dato = null;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Ubicaciones WHERE IdUbicacion=?",
                    new String[]{String.valueOf(idUbicacion)});
            if (cursor.moveToFirst()) {
                dato = LeerUbicacion(cursor);
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return dato;
    }

    public boolean LimpiarUbicaciones() {
        boolean r = false;
        try {
            dbWriter.execSQL("DELETE FROM Ubicaciones");
            r = true;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    private Ubicacion LeerUbicacion(Cursor cursor) {
        Ubicacion dato = new Ubicacion();
        dato.setIdUbicacion(cursor.getInt(0));
        dato.setClave(cursor.getString(1));
        dato.setDescripcion(cursor.getString(2));
        dato.setActivo(cursor.getInt(3) == 1);
        dato.setFechaAlta(ParsearFecha(cursor.getString(4)));
        dato.setFechaMod(ParsearFecha(cursor.getString(5)));
        dato.setUsuarioAlta(cursor.getString(6));
        dato.setUsuarioMod(cursor.getString(7));
        return dato;
    }

    /* =========================== ACTIVOS =========================== */

    public boolean InsertarActivo(Activo activo) {
        boolean r = false;
        try {
            ContentValues values = new ContentValues();
            values.put("IdActivo", activo.getIdActivo());
            values.put("CodigoActivo", activo.getCodigoActivo());
            values.put("Descripcion", activo.getDescripcion());
            values.put("EPC", activo.getEPC());
            values.put("Barcode", activo.getBarcode());
            values.put("IdUbicacion", activo.getIdUbicacion());
            values.put("Marca", activo.getMarca());
            values.put("Modelo", activo.getModelo());
            values.put("NumeroSerie", activo.getNumeroSerie());
            values.put("Activo", activo.getActivo() ? 1 : 0);
            values.put("FechaAlta", FormatearFecha(activo.getFechaAlta()));
            values.put("FechaMod", FormatearFecha(activo.getFechaMod()));
            values.put("UsuarioAlta", activo.getUsuarioAlta());
            values.put("UsuarioMod", activo.getUsuarioMod());
            r = dbWriter.insert("Activos", null, values) != -1;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    public ArrayList<Activo> ObtenerActivos() {
        ArrayList<Activo> datos = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Activos", null);
            if (cursor.moveToFirst()) {
                do {
                    datos.add(LeerActivo(cursor));
                } while (cursor.moveToNext());
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return datos;
    }

    public Activo ObtenerActivoPorId(int idActivo) {
        Activo dato = null;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Activos WHERE IdActivo=?",
                    new String[]{String.valueOf(idActivo)});
            if (cursor.moveToFirst()) {
                dato = LeerActivo(cursor);
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return dato;
    }

    public Activo ObtenerActivoPorEPC(String epc) {
        Activo dato = null;
        Cursor cursor = null;
        try {
            // Solo activos vigentes cuentan como "encontrados" durante un inventario
            cursor = dbReader.rawQuery("SELECT * FROM Activos WHERE EPC=? AND Activo=1",
                    new String[]{epc});
            if (cursor.moveToFirst()) {
                dato = LeerActivo(cursor);
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return dato;
    }

    public Activo ObtenerActivoPorBarcode(String barcode) {
        Activo dato = null;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Activos WHERE Barcode=? AND Activo=1",
                    new String[]{barcode});
            if (cursor.moveToFirst()) {
                dato = LeerActivo(cursor);
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return dato;
    }

    public ArrayList<Activo> ObtenerActivosPorUbicacion(int idUbicacion) {
        ArrayList<Activo> datos = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Activos WHERE IdUbicacion=?",
                    new String[]{String.valueOf(idUbicacion)});
            if (cursor.moveToFirst()) {
                do {
                    datos.add(LeerActivo(cursor));
                } while (cursor.moveToNext());
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return datos;
    }

    public boolean LimpiarActivos() {
        boolean r = false;
        try {
            dbWriter.execSQL("DELETE FROM Activos");
            r = true;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    private Activo LeerActivo(Cursor cursor) {
        Activo dato = new Activo();
        dato.setIdActivo(cursor.getInt(0));
        dato.setCodigoActivo(cursor.getString(1));
        dato.setDescripcion(cursor.getString(2));
        dato.setEPC(cursor.getString(3));
        dato.setBarcode(cursor.getString(4));
        dato.setIdUbicacion(cursor.isNull(5) ? null : cursor.getInt(5));
        dato.setMarca(cursor.getString(6));
        dato.setModelo(cursor.getString(7));
        dato.setNumeroSerie(cursor.getString(8));
        dato.setActivo(cursor.getInt(9) == 1);
        dato.setFechaAlta(ParsearFecha(cursor.getString(10)));
        dato.setFechaMod(ParsearFecha(cursor.getString(11)));
        dato.setUsuarioAlta(cursor.getString(12));
        dato.setUsuarioMod(cursor.getString(13));
        return dato;
    }

    /* =========================== INVENTARIOS =========================== */

    public long CrearInventario(Inventario inventario) {
        long id = -1;
        try {
            inventario.setIdInventario(ObtenerSiguienteIdInventario());

            ContentValues values = new ContentValues();
            values.put("IdInventario", inventario.getIdInventario());
            values.put("FechaInventario", FormatearFecha(inventario.getFechaInventario()));
            values.put("IdUsuario", inventario.getIdUsuario());
            values.put("IdUbicacion", inventario.getIdUbicacion());
            values.put("Estatus", inventario.getEstatus());
            values.put("Sincronizado", inventario.getSincronizado() ? 1 : 0);
            values.put("Activo", inventario.getActivo() ? 1 : 0);
            values.put("FechaAlta", FormatearFecha(inventario.getFechaAlta()));
            values.put("FechaMod", FormatearFecha(inventario.getFechaMod()));
            values.put("UsuarioAlta", inventario.getUsuarioAlta());
            values.put("UsuarioMod", inventario.getUsuarioMod());

            id = dbWriter.insert("Inventarios", null, values);
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return id;
    }

    private int ObtenerSiguienteIdInventario() {
        int siguiente = 1;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT MAX(IdInventario) FROM Inventarios", null);
            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                siguiente = cursor.getInt(0) + 1;
            }
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return siguiente;
    }

    public ArrayList<Inventario> ObtenerInventarios() {
        ArrayList<Inventario> datos = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Inventarios ORDER BY IdInventario DESC", null);
            if (cursor.moveToFirst()) {
                do {
                    datos.add(LeerInventario(cursor));
                } while (cursor.moveToNext());
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return datos;
    }

    public Inventario ObtenerInventarioPorId(int idInventario) {
        Inventario dato = null;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Inventarios WHERE IdInventario=?",
                    new String[]{String.valueOf(idInventario)});
            if (cursor.moveToFirst()) {
                dato = LeerInventario(cursor);
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return dato;
    }

    public boolean ActualizarInventario(Inventario inventario) {
        boolean r = false;
        try {
            ContentValues values = new ContentValues();
            values.put("FechaInventario", FormatearFecha(inventario.getFechaInventario()));
            values.put("IdUsuario", inventario.getIdUsuario());
            values.put("IdUbicacion", inventario.getIdUbicacion());
            values.put("Estatus", inventario.getEstatus());
            values.put("Sincronizado", inventario.getSincronizado() ? 1 : 0);
            values.put("Activo", inventario.getActivo() ? 1 : 0);
            values.put("FechaMod", FormatearFecha(inventario.getFechaMod()));
            values.put("UsuarioMod", inventario.getUsuarioMod());

            int filas = dbWriter.update("Inventarios", values, "IdInventario=?",
                    new String[]{String.valueOf(inventario.getIdInventario())});
            r = filas > 0;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    public ArrayList<Inventario> ObtenerInventariosPendientes() {
        ArrayList<Inventario> datos = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM Inventarios WHERE Sincronizado=0", null);
            if (cursor.moveToFirst()) {
                do {
                    datos.add(LeerInventario(cursor));
                } while (cursor.moveToNext());
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return datos;
    }

    public boolean MarcarInventarioSincronizado(int idInventario) {
        boolean r = false;
        try {
            ContentValues values = new ContentValues();
            values.put("Sincronizado", 1);
            values.put("FechaMod", FormatearFecha(new Date()));

            int filas = dbWriter.update("Inventarios", values, "IdInventario=?",
                    new String[]{String.valueOf(idInventario)});
            r = filas > 0;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    private Inventario LeerInventario(Cursor cursor) {
        Inventario dato = new Inventario();
        dato.setIdInventario(cursor.getInt(0));
        dato.setFechaInventario(ParsearFecha(cursor.getString(1)));
        dato.setIdUsuario(cursor.getInt(2));
        dato.setIdUbicacion(cursor.isNull(3) ? null : cursor.getInt(3));
        dato.setEstatus(cursor.getString(4));
        dato.setSincronizado(cursor.getInt(5) == 1);
        dato.setActivo(cursor.getInt(6) == 1);
        dato.setFechaAlta(ParsearFecha(cursor.getString(7)));
        dato.setFechaMod(ParsearFecha(cursor.getString(8)));
        dato.setUsuarioAlta(cursor.getString(9));
        dato.setUsuarioMod(cursor.getString(10));
        return dato;
    }

    /* =========================== INVENTARIO DETALLE =========================== */

    public boolean AgregarDetalleInventario(InventarioDetalle detalle) {
        boolean r = false;
        try {
            ContentValues values = new ContentValues();
            values.put("IdInventario", detalle.getIdInventario());
            values.put("IdActivo", detalle.getIdActivo());
            values.put("EPC", detalle.getEPC());
            values.put("Barcode", detalle.getBarcode());
            values.put("TipoLectura", detalle.getTipoLectura());
            values.put("FechaLectura", FormatearFecha(detalle.getFechaLectura()));
            values.put("Cantidad", detalle.getCantidad());
            values.put("Sincronizado", detalle.getSincronizado() ? 1 : 0);
            values.put("Activo", detalle.getActivo() ? 1 : 0);
            values.put("FechaAlta", FormatearFecha(detalle.getFechaAlta()));
            values.put("FechaMod", FormatearFecha(detalle.getFechaMod()));
            values.put("UsuarioAlta", detalle.getUsuarioAlta());
            values.put("UsuarioMod", detalle.getUsuarioMod());

            // insertOrThrow para que el indice unico (IdInventario, IdActivo) rechace duplicados
            r = dbWriter.insertOrThrow("InventarioDetalle", null, values) != -1;
            Error = "";
        } catch (Exception e) {
            // Choca con el indice unico: el activo ya estaba registrado en este inventario
            Error = e.getMessage();
            r = false;
        }
        return r;
    }

    public ArrayList<InventarioDetalle> ObtenerDetalleInventario(int idInventario) {
        ArrayList<InventarioDetalle> datos = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM InventarioDetalle WHERE IdInventario=?",
                    new String[]{String.valueOf(idInventario)});
            if (cursor.moveToFirst()) {
                do {
                    datos.add(LeerInventarioDetalle(cursor));
                } while (cursor.moveToNext());
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return datos;
    }

    public boolean ExisteActivoEnInventario(int idInventario, int idActivo) {
        boolean existe = false;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery(
                    "SELECT 1 FROM InventarioDetalle WHERE IdInventario=? AND IdActivo=? LIMIT 1",
                    new String[]{String.valueOf(idInventario), String.valueOf(idActivo)});
            existe = cursor.moveToFirst();
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return existe;
    }

    public InventarioDetalle ObtenerDetallePorActivo(int idInventario, int idActivo) {
        InventarioDetalle dato = null;
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery(
                    "SELECT * FROM InventarioDetalle WHERE IdInventario=? AND IdActivo=? LIMIT 1",
                    new String[]{String.valueOf(idInventario), String.valueOf(idActivo)});
            if (cursor.moveToFirst()) {
                dato = LeerInventarioDetalle(cursor);
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        } finally {
            if (cursor != null) cursor.close();
        }
        return dato;
    }

    public boolean ActualizarDetalle(InventarioDetalle detalle) {
        boolean r = false;
        try {
            ContentValues values = new ContentValues();
            values.put("EPC", detalle.getEPC());
            values.put("Barcode", detalle.getBarcode());
            values.put("TipoLectura", detalle.getTipoLectura());
            values.put("FechaLectura", FormatearFecha(detalle.getFechaLectura()));
            values.put("Cantidad", detalle.getCantidad());
            values.put("Sincronizado", detalle.getSincronizado() ? 1 : 0);
            values.put("Activo", detalle.getActivo() ? 1 : 0);
            values.put("FechaMod", FormatearFecha(detalle.getFechaMod()));
            values.put("UsuarioMod", detalle.getUsuarioMod());

            int filas = dbWriter.update("InventarioDetalle", values, "IdInventarioDetalle=?",
                    new String[]{String.valueOf(detalle.getIdInventarioDetalle())});
            r = filas > 0;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    public ArrayList<InventarioDetalle> ObtenerDetallesPendientes() {
        ArrayList<InventarioDetalle> datos = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = dbReader.rawQuery("SELECT * FROM InventarioDetalle WHERE Sincronizado=0", null);
            if (cursor.moveToFirst()) {
                do {
                    datos.add(LeerInventarioDetalle(cursor));
                } while (cursor.moveToNext());
            }
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
        return datos;
    }

    public boolean MarcarDetalleSincronizado(int idInventarioDetalle) {
        boolean r = false;
        try {
            ContentValues values = new ContentValues();
            values.put("Sincronizado", 1);
            values.put("FechaMod", FormatearFecha(new Date()));

            int filas = dbWriter.update("InventarioDetalle", values, "IdInventarioDetalle=?",
                    new String[]{String.valueOf(idInventarioDetalle)});
            r = filas > 0;
            Error = "";
        } catch (Exception e) {
            Error = e.getMessage();
        }
        return r;
    }

    private InventarioDetalle LeerInventarioDetalle(Cursor cursor) {
        InventarioDetalle dato = new InventarioDetalle();
        dato.setIdInventarioDetalle(cursor.getInt(0));
        dato.setIdInventario(cursor.getInt(1));
        dato.setIdActivo(cursor.isNull(2) ? null : cursor.getInt(2));
        dato.setEPC(cursor.getString(3));
        dato.setBarcode(cursor.getString(4));
        dato.setTipoLectura(cursor.getString(5));
        dato.setFechaLectura(ParsearFecha(cursor.getString(6)));
        dato.setCantidad(cursor.getInt(7));
        dato.setSincronizado(cursor.getInt(8) == 1);
        dato.setActivo(cursor.getInt(9) == 1);
        dato.setFechaAlta(ParsearFecha(cursor.getString(10)));
        dato.setFechaMod(ParsearFecha(cursor.getString(11)));
        dato.setUsuarioAlta(cursor.getString(12));
        dato.setUsuarioMod(cursor.getString(13));
        return dato;
    }
}
