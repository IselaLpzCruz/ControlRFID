package com.example.minicontrolrfid.DB;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.example.minicontrolrfid.Entidades.Activo;
import com.example.minicontrolrfid.Entidades.Inventario;
import com.example.minicontrolrfid.Entidades.InventarioDetalle;
import com.example.minicontrolrfid.Entidades.Ubicacion;
import com.example.minicontrolrfid.Entidades.Usuarios;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;

public class Api {

    public String Token = "";
    public String Error = "";
    private Context context;

    // PENDIENTE DE DEFINIR ENDPOINT: reemplazar por la URL real y las credenciales
    // del servicio antes de salir a pruebas/producción. No quemar credenciales reales aquí.
    final String url = "https://novo-api-develop-20410.azurewebsites.net/";
    final String UserJwt = "ControlRFIDUser";
    final String pswjwt = "ControlRFID3v2025?";



    // Nuestras entidades guardan fechas como Date (no String como en Control Inventario).
    // SQL Server (FOR JSON) devuelve los datetime en formato ISO con 'T' (ej. "2026-09-02T00:00:00"),
    // no "yyyy-MM-dd HH:mm:ss" - se prueban varios formatos porque el sufijo de milisegundos varia
    // segun el tipo de columna (datetime vs datetime2).
    private static final String[] FORMATOS_FECHA = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
    };

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, jsonContext) -> {
                String valor = json.getAsString();
                for (String formato : FORMATOS_FECHA) {
                    try {
                        return new SimpleDateFormat(formato, Locale.getDefault()).parse(valor);
                    } catch (ParseException ignored) {
                    }
                }
                Log.w("Api", "No se pudo parsear la fecha: " + valor);
                return null;
            })
            .create();

    public Api(@Nullable Context context) {
        this.context = context;
    }

    /* =========================== LOGIN =========================== */

    public Usuarios Login(String usuario, String passwordHasheado) {
        final Usuarios[] usr = {null};
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Proc_Usuarios.Accion=4 filtra por @Usuario y trae como maximo una fila;
                    // el password se valida aqui en la app (el SP no lo compara)
                    ArrayList<Usuarios> usuarios = ObtenerUsuarioPorNombre(usuario);

                    if (usuarios != null) {
                        for (Usuarios u : usuarios) {
                            if (u.getUsuario() != null
                                    && u.getUsuario().equalsIgnoreCase(usuario)
                                    && passwordHasheado.equals(u.getPassword())
                                    && u.getActivo()) {
                                usr[0] = u;
                                break;
                            }
                        }
                    }

                    if (usr[0] == null && (Error == null || Error.isEmpty())) {
                        Error = "Usuario o contraseña incorrectos";
                    }
                } catch (Exception e) {
                    Error = e.getMessage();
                }
            }
        });
        hilo.start();
        try {
            hilo.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return usr[0];
    }

    /* =========================== CATALOGOS (SINCRONIZACION) =========================== */

    public void SincronizarCatalogos(DBLocal dbLocal, SyncCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Error = "";
                try {
                    ObtenerTocken();

                    ArrayList<Usuarios> usuarios = ObtenerUsuarios();
                    ArrayList<Ubicacion> ubicaciones = ObtenerUbicaciones();
                    ArrayList<Activo> activos = ObtenerActivos();

                    // Si algun catalogo no se pudo descargar (sin conexion, respuesta invalida, etc.)
                    // no se toca lo que ya habia en local y se reporta el error - nunca se confirma exito a medias
                    if (usuarios == null || ubicaciones == null || activos == null) {
                        if (Error == null || Error.isEmpty()) {
                            Error = "No se pudo descargar el catálogo (sin conexión o respuesta inválida)";
                        }
                        return;
                    }

                    dbLocal.LimpiarUsuarios();
                    for (Usuarios u : usuarios) {
                        dbLocal.InsertarUsuario(u);
                    }

                    dbLocal.LimpiarUbicaciones();
                    for (Ubicacion u : ubicaciones) {
                        dbLocal.InsertarUbicacion(u);
                    }

                    dbLocal.LimpiarActivos();
                    for (Activo a : activos) {
                        dbLocal.InsertarActivo(a);
                    }
                } catch (Exception e) {
                    Error = e.getMessage();
                } finally {
                    callBack.onSyncComplete();
                }
            }
        }).start();
    }

    private ArrayList<Usuarios> ObtenerUsuarios() {
        try {
            Peticion peticion = new Peticion("Proc_Usuarios");
            peticion.AgregarParametro("Accion", "1", 8);
            Respuesta r = LlamarProcedimientoSync(peticion);

            if (r == null || !r.isExito() || r.getResultado() == null) {
                Error = (r != null && r.getError() != null) ? r.getError() : "Sin respuesta del servidor";
                return null;
            }

            Type tipo = new TypeToken<ArrayList<Usuarios>>() {
            }.getType();
            return GSON.fromJson(LimpiarJsonALista(r.getResultado()), tipo);
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        }
    }

    private ArrayList<Usuarios> ObtenerUsuarioPorNombre(String usuario) {
        try {
            Peticion peticion = new Peticion("Proc_Usuarios");
            peticion.AgregarParametro("Accion", "4", 8);
            peticion.AgregarParametro("Usuario", usuario, 22);
            Respuesta r = LlamarProcedimientoSync(peticion);

            if (r == null || !r.isExito() || r.getResultado() == null) {
                Error = (r != null && r.getError() != null) ? r.getError() : "Sin respuesta del servidor";
                return null;
            }

            Type tipo = new TypeToken<ArrayList<Usuarios>>() {
            }.getType();
            return GSON.fromJson(LimpiarJsonALista(r.getResultado()), tipo);
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        }
    }

    private ArrayList<Ubicacion> ObtenerUbicaciones() {
        try {
            Peticion peticion = new Peticion("Proc_Ubicaciones"); // PENDIENTE DE DEFINIR ENDPOINT
            peticion.AgregarParametro("Accion", "1", 8);
            Respuesta r = LlamarProcedimientoSync(peticion);

            if (r == null || !r.isExito() || r.getResultado() == null) {
                Error = (r != null && r.getError() != null) ? r.getError() : "Sin respuesta del servidor";
                return null;
            }

            Type tipo = new TypeToken<ArrayList<Ubicacion>>() {
            }.getType();
            return GSON.fromJson(LimpiarJsonALista(r.getResultado()), tipo);
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        }
    }

    private ArrayList<Activo> ObtenerActivos() {
        try {
            Peticion peticion = new Peticion("Proc_Activos"); // PENDIENTE DE DEFINIR ENDPOINT
            peticion.AgregarParametro("Accion", "1", 8);
            Respuesta r = LlamarProcedimientoSync(peticion);

            if (r == null || !r.isExito() || r.getResultado() == null) {
                Error = (r != null && r.getError() != null) ? r.getError() : "Sin respuesta del servidor";
                return null;
            }

            Type tipo = new TypeToken<ArrayList<Activo>>() {
            }.getType();
            return GSON.fromJson(LimpiarJsonALista(r.getResultado()), tipo);
        } catch (Exception e) {
            Error = e.getMessage();
            return null;
        }
    }

    /* =========================== INVENTARIOS (SUBIR PENDIENTES) =========================== */

    public void EnviarInventariosPendientes(ArrayList<Inventario> inventariosPendientes, DBLocal dbLocal, SyncCallBack callBack) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Error = "";
                try {
                    for (Inventario inventario : inventariosPendientes) {

                        ArrayList<InventarioDetalle> detalle = dbLocal.ObtenerDetalleInventario(inventario.getIdInventario());

                        Peticion peticion = new Peticion("Proc_Inventarios"); // PENDIENTE DE DEFINIR ENDPOINT
                        peticion.AgregarParametro("Accion", "2", 8);
                        peticion.AgregarParametro("XML", ObtenerXMLInventario(inventario, detalle), 18);

                        Respuesta r = LlamarProcedimientoSync(peticion);

                        // Solo se marca sincronizado si el servidor confirmo exito - nunca antes
                        if (r != null && r.isExito()) {
                            dbLocal.MarcarInventarioSincronizado(inventario.getIdInventario());
                            if (detalle != null) {
                                for (InventarioDetalle d : detalle) {
                                    dbLocal.MarcarDetalleSincronizado(d.getIdInventarioDetalle());
                                }
                            }
                        } else {
                            Error = (r != null && r.getError() != null)
                                    ? r.getError()
                                    : "Error al subir el inventario " + inventario.getIdInventario();
                        }
                    }
                } catch (Exception e) {
                    Error = e.getMessage();
                } finally {
                    callBack.onSyncComplete();
                }
            }
        }).start();
    }

    private String ObtenerXMLInventario(Inventario inventario, ArrayList<InventarioDetalle> detalle) throws Exception {
        DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder build = dFact.newDocumentBuilder();
        Document doc = build.newDocument();

        Element root = doc.createElement("ROOT");
        doc.appendChild(root);

        Element nodoInventario = doc.createElement("Inventario");
        root.appendChild(nodoInventario);

        agregarElemento(doc, nodoInventario, "IdInventario", String.valueOf(inventario.getIdInventario()));
        agregarElemento(doc, nodoInventario, "IdUsuario", String.valueOf(inventario.getIdUsuario()));
        agregarElemento(doc, nodoInventario, "IdUbicacion", String.valueOf(inventario.getIdUbicacion()));
        agregarElemento(doc, nodoInventario, "Estatus", inventario.getEstatus());

        if (detalle != null && !detalle.isEmpty()) {
            Element nodoDetalleRaiz = doc.createElement("InventarioDetalle");
            nodoInventario.appendChild(nodoDetalleRaiz);

            for (InventarioDetalle d : detalle) {
                Element detNodo = doc.createElement("Detalle");
                nodoDetalleRaiz.appendChild(detNodo);

                agregarElemento(doc, detNodo, "IdActivo", String.valueOf(d.getIdActivo()));
                agregarElemento(doc, detNodo, "EPC", d.getEPC());
                agregarElemento(doc, detNodo, "Barcode", d.getBarcode());
                agregarElemento(doc, detNodo, "TipoLectura", d.getTipoLectura());
                agregarElemento(doc, detNodo, "Cantidad", String.valueOf(d.getCantidad()));
            }
        }

        TransformerFactory tranFactory = TransformerFactory.newInstance();
        Transformer aTransformer = tranFactory.newTransformer();
        aTransformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource source = new DOMSource(doc);
        StringWriter w = new StringWriter();
        Result dest = new StreamResult(w);
        aTransformer.transform(source, dest);
        return w.toString();
    }

    private void agregarElemento(Document doc, Element parent, String nombre, String valor) {
        Element elemento = doc.createElement(nombre);
        elemento.appendChild(doc.createTextNode(valor != null ? valor : ""));
        parent.appendChild(elemento);
    }

    /* =========================== INFRAESTRUCTURA (TOKEN / LLAMADA GENERICA) =========================== */

    private void ObtenerTocken() {
        try {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            Credenciales credenciales = new Credenciales();
            credenciales.setNombreDeUsuario(UserJwt);
            credenciales.setPassword(pswjwt);

            apiService service = retrofit.create(apiService.class);
            Call<RespuestaToken> response = service.loginAuth(credenciales);
            try {
                RespuestaToken r = response.execute().body();
                if (r != null && r.getTokenString() != null) {
                    Token = "Bearer " + r.getTokenString();
                } else {
                    Error = "No se pudo obtener el token de autenticación";
                }
            } catch (IOException e) {
                Error = e.getMessage();
                e.printStackTrace();
            }
        } catch (Exception e) {
            Error = e.getMessage();
        }
    }

    private String LimpiarJsonALista(String json) {
        int t = json.length();
        return json.substring(9, t - 1);
    }

    private String LimpiarJsonAObjeto(String json) {
        int t = json.length();
        return json.substring(10, t - 2);
    }

    private Respuesta LlamarProcedimientoSync(Peticion peticion) throws IOException {
        if (Token.equals("")) {
            ObtenerTocken();
        }

        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        apiService service = retrofit.create(apiService.class);
        Call<Respuesta> response = service.llamarProcedimiento(Token, "application/json", peticion);

        try {
            Response<Respuesta> resp = response.execute();

            if (!resp.isSuccessful()) {
                String errorBody = resp.errorBody() != null ? resp.errorBody().string() : "Sin mensaje de error";
                Log.e("Api", "Error HTTP " + resp.code() + ": " + errorBody);
                return null;
            }

            return resp.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
