package com.example.minicontrolrfid.Activities;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.minicontrolrfid.R;
import com.example.minicontrolrfid.UHF.UHFBaseActivity;
import com.pda.rfid.EPCModel;
import com.pda.rfid.IAsynchronousMessage;
import com.pda.rfid.uhf.UHFReader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LecturaRFIDActivity extends UHFBaseActivity implements IAsynchronousMessage
{
    private TextView tvEstado;
    private TextView tvTotal;
    private ListView lvEpcs;
    private Button btnLeer;
    private Button btnLimpiar;

    private final ArrayList<String> listaEpcs = new ArrayList<>();
    private final HashSet<String> epcsLeidos = new HashSet<>();

    private ArrayAdapter<String> adapter;

    private boolean lectorInicializado = false;
    private boolean leyendo = false;
    private boolean triggerPresionado = false;

    private ToneGenerator toneGenerator;
    private final ExecutorService executorUHF = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectura_rfid);

        tvEstado = findViewById(R.id.tvEstado);
        tvTotal = findViewById(R.id.tvTotal);
        lvEpcs = findViewById(R.id.lvEpcs);
        btnLeer = findViewById(R.id.btnLeer);
        btnLimpiar = findViewById(R.id.btnLimpiar);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaEpcs);
        lvEpcs.setAdapter(adapter);

        toneGenerator = new ToneGenerator(AudioManager.STREAM_SYSTEM, ToneGenerator.MAX_VOLUME);

        btnLeer.setEnabled(false);

        btnLeer.setOnClickListener(v ->
        {
            if (!leyendo)
            {
                IniciarLectura();
            }
            else
            {
                DetenerLectura();
            }
        });

        btnLimpiar.setOnClickListener(v -> LimpiarLecturas());

        InicializarLector();
    }

    private void InicializarLector()
    {
        tvEstado.setText("Inicializando lector RFID...");
        btnLeer.setEnabled(false);

        executorUHF.execute(() ->
        {
            try
            {
                Log.d("RFID", "Iniciando módulo UHF...");

                boolean inicializado = UHF_Init(this);

                Log.d("RFID", "Resultado UHF_Init: " + inicializado);

                if (!inicializado)
                {
                    runOnUiThread(() ->
                    {
                        tvEstado.setText("No fue posible iniciar el lector RFID");
                        btnLeer.setEnabled(false);
                    });

                    return;
                }

                Log.d("RFID", "Obteniendo propiedades UHF...");

                UHF_GetReaderProperty();

                Log.d("RFID", "Deteniendo lectura previa...");

                CLReader.Stop();

                Log.d("RFID", "Configurando actualización de etiquetas...");

                UHF_SetTagUpdateParam();

                lectorInicializado = true;

                runOnUiThread(() ->
                {
                    tvEstado.setText("Lector RFID listo");
                    btnLeer.setEnabled(true);
                });

                Log.d("RFID", "Lector RFID inicializado correctamente");
            }
            catch (Exception e)
            {
                Log.e("RFID", "Error al inicializar lector RFID", e);

                lectorInicializado = false;

                runOnUiThread(() ->
                {
                    tvEstado.setText("Error al inicializar lector RFID");
                    btnLeer.setEnabled(false);
                });
            }
        });
    }

    private void IniciarLectura()
    {
        if (!lectorInicializado || leyendo)
        {
            return;
        }

        leyendo = true;

        tvEstado.setText("Leyendo etiquetas...");
        btnLeer.setText("Detener");

        executorUHF.execute(() ->
        {
            try
            {
                Log.d("RFID", "Iniciando GetEPC");

                UHFReader._Tag6C.GetEPC(_NowAntennaNo, 1);
            }
            catch (Exception e)
            {
                Log.e("RFID", "Error al iniciar lectura RFID", e);

                runOnUiThread(this::DetenerLectura);
            }
        });
    }

    private void DetenerLectura()
    {
        if (!leyendo)
        {
            return;
        }

        leyendo = false;

        tvEstado.setText("Lectura detenida");
        btnLeer.setText("Leer");

        executorUHF.execute(() ->
        {
            try
            {
                CLReader.Stop();
            }
            catch (Exception e)
            {
                Log.e("RFID", "Error al detener lectura RFID", e);
            }
        });
    }

    private void LimpiarLecturas()
    {
        epcsLeidos.clear();
        listaEpcs.clear();

        adapter.notifyDataSetChanged();

        tvTotal.setText("Etiquetas encontradas: 0");
    }

    @Override
    public void OutPutEPC(EPCModel model)
    {
        if (!leyendo || model == null || model._EPC == null)
        {
            return;
        }

        String epc = model._EPC.trim();

        if (epc.isEmpty())
        {
            return;
        }

        if (!epcsLeidos.add(epc))
        {
            return;
        }

        Log.d("RFID", "EPC encontrado: " + epc);

        runOnUiThread(() ->
        {
            listaEpcs.add(epc);
            adapter.notifyDataSetChanged();

            tvTotal.setText("Etiquetas encontradas: " + listaEpcs.size());

            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 80);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        Log.d("RFID_TRIGGER", "KeyDown: " + keyCode);

        if (EsTriggerRFID(keyCode))
        {
            if (!triggerPresionado)
            {
                triggerPresionado = true;
                IniciarLectura();
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        Log.d("RFID_TRIGGER", "KeyUp: " + keyCode);

        if (EsTriggerRFID(keyCode))
        {
            triggerPresionado = false;
            DetenerLectura();

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    private boolean EsTriggerRFID(int keyCode)
    {
        return keyCode == KeyEvent.KEYCODE_F1 ||
                keyCode == KeyEvent.KEYCODE_F5 ||
                keyCode == KeyEvent.KEYCODE_F9 ||
                keyCode == KeyEvent.KEYCODE_BREAK || // Gatillo fisico HY830
                keyCode == 285 ||
                keyCode == 286;
    }

    @Override
    protected void onDestroy()
    {
        try
        {
            if (leyendo)
            {
                CLReader.Stop();
            }

            UHF_Dispose();

            if (toneGenerator != null)
            {
                toneGenerator.release();
            }

            executorUHF.shutdownNow();
        }
        catch (Exception e)
        {
            Log.e("RFID", "Error cerrando UHF", e);
        }

        super.onDestroy();
    }
}