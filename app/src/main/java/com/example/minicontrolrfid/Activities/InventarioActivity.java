package com.example.minicontrolrfid.Activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.minicontrolrfid.Adapters.ActivoAdapter;
import com.example.minicontrolrfid.DB.DBLocal;
import com.example.minicontrolrfid.Entidades.Activo;
import com.example.minicontrolrfid.Entidades.Inventario;
import com.example.minicontrolrfid.Entidades.InventarioDetalle;
import com.example.minicontrolrfid.Entidades.Ubicacion;
import com.example.minicontrolrfid.R;
import com.example.minicontrolrfid.UHF.UHFBaseActivity;
import com.example.minicontrolrfid.Util.Sesion;
import com.pda.rfid.EPCModel;
import com.pda.rfid.IAsynchronousMessage;
import com.pda.rfid.uhf.UHFReader;
import com.pda.scanner.ScanReader;
import com.pda.scanner.Scanner;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventarioActivity extends UHFBaseActivity implements IAsynchronousMessage {

    private static final int REQUEST_CODE_CAMERA_BARCODE = 4821;

    private DBLocal dbLocal;
    private int idInventario;
    private int idUbicacion;
    private int idUsuarioActual;
    private String usuarioActual;

    private boolean usandoBarcode = false;
    private boolean leyendoRFID = false;
    private boolean isKeyDown = false;

    private final HashSet<String> epcsLeidosRFID = new HashSet<>();
    private final ArrayList<Activo> listaMostrada = new ArrayList<>();
    private ActivoAdapter adapter;

    private Scanner scanReader = ScanReader.getScannerInstance();

    private final ExecutorService executorUHF = Executors.newSingleThreadExecutor();
    private final ExecutorService executorProcesamiento = Executors.newSingleThreadExecutor();

    private TextView txtUbicacionInv;
    private TextView txtUsuarioInv;
    private TextView txtModoInv;
    private TextView txtTotalInv;
    private ListView lvActivosInv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_inventario);

        idUbicacion = Sesion.ObtenerUbicacionSeleccionada(this);
        idUsuarioActual = Sesion.ObtenerIdUsuarioActual(this);
        usuarioActual = Sesion.ObtenerUsuarioActual(this);

        dbLocal = new DBLocal(this);

        if (idUbicacion == 0) {
            Toast.makeText(this, "Selecciona una ubicación primero", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Ubicacion ubicacion = dbLocal.ObtenerUbicacionPorId(idUbicacion);

        Inventario inventario = new Inventario();
        inventario.setFechaInventario(new Date());
        inventario.setIdUsuario(idUsuarioActual);
        inventario.setIdUbicacion(idUbicacion);
        inventario.setEstatus("ABIERTO");
        inventario.setSincronizado(false);
        inventario.setActivo(true);
        inventario.setFechaAlta(new Date());
        inventario.setUsuarioAlta(usuarioActual);

        idInventario = (int) dbLocal.CrearInventario(inventario);

        txtUbicacionInv = findViewById(R.id.txtUbicacionInv);
        txtUsuarioInv = findViewById(R.id.txtUsuarioInv);
        txtModoInv = findViewById(R.id.txtModoInv);
        txtTotalInv = findViewById(R.id.txtTotalInv);
        lvActivosInv = findViewById(R.id.lvActivosInv);

        txtUbicacionInv.setText(ubicacion != null ? ubicacion.getDescripcion() : "");
        txtUsuarioInv.setText(usuarioActual);
        txtModoInv.setText(getString(R.string.txt_LectorRFID));

        adapter = new ActivoAdapter(this, listaMostrada, null);
        lvActivosInv.setAdapter(adapter);

        findViewById(R.id.btnCambiarModoInv).setOnClickListener(v -> CambiarModo());
        findViewById(R.id.btnFinalizarInv).setOnClickListener(v -> FinalizarInventario());

        ActualizarTotal();
    }

    protected void Init() {
        if (!UHF_Init(this)) {
            showMsg(getString(R.string.uhf_low_power_info), (dialog, which) -> InventarioActivity.this.finish());
        } else {
            try {
                UHF_GetReaderProperty();
                Thread.sleep(20);
                CLReader.Stop();
                Thread.sleep(20);
                UHF_SetTagUpdateParam();
            } catch (Exception ignored) {
            }
        }
    }

    protected void Dispose() {
        leyendoRFID = false;
        CerrarScannerBarcode();
        UHF_Dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dispose();
    }

    @Override
    protected void onDestroy() {
        executorUHF.shutdownNow();
        executorProcesamiento.shutdownNow();
        super.onDestroy();
    }

    /* =========================== MODO RFID / BARCODE =========================== */

    private void CambiarModo() {
        if (!usandoBarcode) {
            usandoBarcode = true;
            txtModoInv.setText(getString(R.string.txt_LectorBarcode));
            AbrirScannerBarcode();
        } else {
            usandoBarcode = false;
            txtModoInv.setText(getString(R.string.txt_LectorRFID));
            CerrarScannerBarcode();
        }
    }

    /* =========================== TRIGGER FISICO (HY820 / HY830) =========================== */

    private boolean EsTriggerRFID(int keyCode) {
        return keyCode == KeyEvent.KEYCODE_F1 ||
                keyCode == KeyEvent.KEYCODE_F5 ||
                keyCode == KeyEvent.KEYCODE_F9 ||
                keyCode == KeyEvent.KEYCODE_BREAK || // HY830
                keyCode == 285 ||
                keyCode == 286;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (EsTriggerRFID(keyCode)) {
            if (!isKeyDown) {
                isKeyDown = true;
                if (usandoBarcode) {
                    LeerBarcode();
                } else {
                    IniciarLecturaRFID();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (EsTriggerRFID(keyCode)) {
            isKeyDown = false;
            if (!usandoBarcode) {
                DetenerLecturaRFID();
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /* =========================== RFID =========================== */

    private void IniciarLecturaRFID() {
        if (leyendoRFID) return;
        leyendoRFID = true;

        executorUHF.execute(() -> {
            try {
                UHFReader._Tag6C.GetEPC(_NowAntennaNo, 1);
            } catch (Exception e) {
                Log.e("INVENTARIO", "Error al iniciar lectura RFID", e);
            }
        });
    }

    private void DetenerLecturaRFID() {
        if (!leyendoRFID) return;
        leyendoRFID = false;

        executorUHF.execute(() -> {
            try {
                CLReader.Stop();
            } catch (Exception e) {
                Log.e("INVENTARIO", "Error al detener lectura RFID", e);
            }
        });
    }

    @Override
    public void OutPutEPC(EPCModel model) {
        if (!leyendoRFID || model == null || model._EPC == null) return;

        String epc = model._EPC.trim();
        if (epc.isEmpty()) return;

        // El lector puede reportar el mismo EPC muchas veces mientras se mantiene el gatillo presionado
        if (!epcsLeidosRFID.add(epc)) return;

        ProcesarLectura(epc, null, "RFID");
    }

    /* =========================== BARCODE =========================== */

    private void AbrirScannerBarcode() {
        try {
            if (scanReader == null) return;

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE_CAMERA_BARCODE);
                return;
            }

            scanReader.open(getApplicationContext());
            scanReader.enableBlockScankey(false);
        } catch (Exception ex) {
            Log.d("BARCODE", "Error al abrir el scanner: " + ex.getMessage());
        }
    }

    private void CerrarScannerBarcode() {
        try {
            if (scanReader != null) {
                scanReader.enableBlockScankey(true);
                scanReader.close();
            }
        } catch (Exception ex) {
            Log.d("BARCODE", "Error al cerrar el scanner: " + ex.getMessage());
        }
    }

    private void LeerBarcode() {
        new Thread(() -> {
            try {
                byte[] datos = scanReader.decode();
                String codigo = null;

                if (datos != null) {
                    String utf8 = new String(datos, Charset.forName("utf8"));
                    if (utf8.contains("�")) {
                        utf8 = new String(datos, Charset.forName("gbk"));
                    }
                    codigo = utf8.trim();
                }

                if (codigo != null && !codigo.isEmpty()) {
                    ProcesarLectura(null, codigo, "BARCODE");
                }
            } catch (Exception ex) {
                Log.d("BARCODE", "Error al leer con el scanner: " + ex.getMessage());
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA_BARCODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (usandoBarcode) {
                    AbrirScannerBarcode();
                }
            } else {
                Toast.makeText(this, "Se requiere permiso de cámara para usar el escáner de código de barras", Toast.LENGTH_LONG).show();
            }
        }
    }

    /* =========================== LOGICA COMPARTIDA (RFID + BARCODE) =========================== */

    private void ProcesarLectura(String epc, String barcode, String tipoLectura) {
        executorProcesamiento.execute(() -> {
            Activo activo = (epc != null) ? dbLocal.ObtenerActivoPorEPC(epc) : dbLocal.ObtenerActivoPorBarcode(barcode);

            if (activo == null) {
                runOnUiThread(() -> MostrarActivoNoEncontrado(epc != null ? epc : barcode));
                return;
            }

            // Control de duplicados a nivel de BD (no solo en memoria): IdInventario + IdActivo
            if (dbLocal.ExisteActivoEnInventario(idInventario, activo.getIdActivo())) {
                return;
            }

            InventarioDetalle detalle = new InventarioDetalle();
            detalle.setIdInventario(idInventario);
            detalle.setIdActivo(activo.getIdActivo());
            detalle.setEPC(epc);
            detalle.setBarcode(barcode);
            detalle.setTipoLectura(tipoLectura);
            detalle.setFechaLectura(new Date());
            detalle.setCantidad(1);
            detalle.setSincronizado(false);
            detalle.setActivo(true);
            detalle.setFechaAlta(new Date());
            detalle.setUsuarioAlta(usuarioActual);

            boolean agregado = dbLocal.AgregarDetalleInventario(detalle);

            if (agregado) {
                runOnUiThread(() -> {
                    listaMostrada.add(activo);
                    adapter.setLista(listaMostrada);
                    ActualizarTotal();
                    playTone(SUCESS_TONE);
                });
            }
        });
    }

    private void MostrarActivoNoEncontrado(String codigo) {
        Toast.makeText(this, getString(R.string.txt_ActivoNoEncontrado) + ": " + codigo, Toast.LENGTH_SHORT).show();
        playTone(ERROR_TONE);
    }

    private void ActualizarTotal() {
        txtTotalInv.setText(getString(R.string.txt_Total) + ": " + listaMostrada.size());
    }

    /* =========================== FINALIZAR =========================== */

    private void FinalizarInventario() {
        Inventario inventario = dbLocal.ObtenerInventarioPorId(idInventario);
        if (inventario != null) {
            inventario.setEstatus("CERRADO");
            inventario.setFechaMod(new Date());
            inventario.setUsuarioMod(usuarioActual);
            dbLocal.ActualizarInventario(inventario);
        }
        finish();
    }
}
