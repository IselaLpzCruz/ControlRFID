package com.example.minicontrolrfid.UHF;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.minicontrolrfid.R;
import com.example.minicontrolrfid.Util.PublicData;
import com.pda.rfid.EPCModel;
import com.pda.rfid.IAsynchronousMessage;
import com.pda.rfid.uhf.UHFReader;

import java.util.HashMap;

public class UHFConfigActivity extends UHFBaseActivity implements
        IAsynchronousMessage {

    private Spinner sp_Configration_Power = null;
    private Spinner sp_PingPongReadTime = null;
    private Spinner sp_PingPongStopTime = null;
    private Spinner sp_Frequency = null;
    private Spinner sp_BaseSpeedType = null;
    private Spinner sp_BaseSpeedQValue = null;
    private Spinner sp_Carrier = null;
    private Spinner sp_TagType = null;
    private EditText edtRSSI;
    private ArrayAdapter<String> adapter = null;
    private String[] powers_G = { "0", "1","2", "3", "4", "5","6", "7", "8", "9","10", "11", "12", "13","14", "15", "16", "17","18", "19", "20", "21","22", "23", "24", "25", "26","27", "28", "29", "30", "31", "32", "33" };

    EPCModel model;
    CheckBox checkboxModoOffline;

    @SuppressLint("UseSparseArrays")
    @SuppressWarnings("serial")
    private HashMap<Integer, Integer> mm_PingPong = new HashMap<Integer, Integer>() {
        {
            put(0, 0);
            put(100, 1);
            put(200, 2);
            put(300, 3);
            put(500, 4);
            put(800, 5);
            put(1000, 6);
            put(5000, 7);
            put(10000, 8);
        }
    };

    //init
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void Init() {
        // do not using power switch in configure
        if (!UHF_Init(this)) { // 打开模块电源失败
            showMsg(getString(R.string.uhf_low_power_info),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            UHFConfigActivity.this.finish();
                        }
                    });
        } else {
            sp_Configration_Power = (Spinner) this
                    .findViewById(R.id.sp_Configration_Power);
            sp_PingPongReadTime = (Spinner) this
                    .findViewById(R.id.sp_PingPongReadTime);
            sp_PingPongStopTime = (Spinner) this
                    .findViewById(R.id.sp_PingPongStopTime);
            sp_Frequency = (Spinner) this.findViewById(R.id.sp_Frequency);
            sp_BaseSpeedType = (Spinner) this
                    .findViewById(R.id.sp_BaseSpeedType);
            sp_BaseSpeedQValue = (Spinner) this
                    .findViewById(R.id.sp_BaseSpeedQValue);
            sp_Carrier = (Spinner) this.findViewById(R.id.sp_Carrier);
            sp_TagType = (Spinner) this.findViewById(R.id.sp_TagType1);
            edtRSSI = (EditText) this.findViewById(R.id.edtRSSI);
            GetBaseBand();// 查询基带速率

            UHF_GetReaderProperty();

            if(_Max_Power > 0)
            {
                String[] powerValue=new String[_Max_Power - _Min_Power + 1];
                for(int i = 0;i < _Max_Power - _Min_Power + 1;i++)
                {
                    powerValue[i] = (_Min_Power + i) + "";
                }
                adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,powerValue);
                sp_Configration_Power.setAdapter(adapter);
            }
            else
            {
                adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,powers_G);
                sp_Configration_Power.setAdapter(adapter);
            }

            GetPower(); // 获取功率
            Button_GetFrequency(null); // 查询频段
            sp_PingPongReadTime.setSelection(mm_PingPong
                    .get(PublicData._PingPong_ReadTime));
            sp_PingPongStopTime.setSelection(mm_PingPong
                    .get(PublicData._PingPong_StopTime));
            //
            if (PublicData._IsCommand6Cor6B.equals("6C")) {
                sp_TagType.setSelection(0);
            } else {
                sp_TagType.setSelection(1);
            }

            edtRSSI.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    try {
                        int rssiValue = Integer.parseInt(edtRSSI.getText().toString().trim());
                        PublicData.RSSI = rssiValue;

                        getSharedPreferences("PreferenciasApp", MODE_PRIVATE)
                                .edit()
                                .putInt("RSSI", rssiValue)
                                .apply();

                    } catch (NumberFormatException e) {
                        PublicData.RSSI = 0;
                        Toast.makeText(this, "El valor debe ser un número válido", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            });



        }
        return;
    }

    // Query current power
    protected boolean GetPower() {
        boolean rt = false;
        int iPower = UHFReader._Config.GetANTPowerParam();
        if(iPower!=-1)
        {
            rt = true;
        }
        try{
            sp_Configration_Power.setSelection(iPower - _Min_Power);
        }
        catch(Exception ex) //set the power to 25
        {
            int antCount = 1;
            if (_NowAntennaNo == 3) {
                antCount = 2;
            }
            UHFReader._Config.SetANTPowerParam(antCount,25);
            sp_Configration_Power.setSelection(25 - _Min_Power);
        }
        return rt;
    }

    //codigo RF

    public void Button_SalirConfigHH(View v){
        finish();
    }
    //Codigo RF

    // Dispose
    protected void Dispose() {
        UHF_Dispose();
    }

    // back
    public void Back(View v) {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // create
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.uhf_configuration );
        //ChangeLayout(getResources().getConfiguration());

        try {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception ex) {
            Log.d("Debug", "The initialization of abnormal:" + ex.getMessage());
        }

        checkboxModoOffline = (CheckBox) findViewById(R.id.checkboxModoOffline);

        checkboxModoOffline.setOnCheckedChangeListener((buttonView, isChecked) -> {
            PublicData.ModoOffLineActivado = isChecked;

            // Guardamos la preferencia cada vez que el usuario lo cambie
            getSharedPreferences("PreferenciasApp", MODE_PRIVATE)
                    .edit()
                    .putBoolean("ModoOffLineActivado", isChecked)
                    .apply();
        });

        showCustomBar("Configuración",
                getString(R.string.str_back), null,
                R.drawable.left, 0,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Back(v);
                    }
                },
                null
        );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //ChangeLayout(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            Init();
            // super.UHF_GetReaderProperty();
            // Actualiza el valor siempre que regrese a la pantalla

            boolean modoOffline = getSharedPreferences("PreferenciasApp", MODE_PRIVATE)
                    .getBoolean("ModoOffLineActivado", false);

            checkboxModoOffline.setChecked(modoOffline);
            PublicData.ModoOffLineActivado = modoOffline;

            // CARGAR RSSI GUARDADO Y MOSTRARLO EN EL EDITTEXT
            int rssiGuardado = getSharedPreferences("PreferenciasApp", MODE_PRIVATE)
                    .getInt("RSSI", 0); // valor por defecto: 0

            edtRSSI.setText(String.valueOf(rssiGuardado));
            PublicData.RSSI = rssiGuardado;

        } catch (Exception ex) {
        }
    }

    @Override
    public void OutPutEPC(EPCModel model) {

    }

    // ------------------------- Button Click -------------------------------
    // GetPower
    public void Button_GetPower(View v) {
        if (isFastClick()) {
            return;
        }
        if (GetPower()) {
            showMsg(getString(R.string.str_success), null);
        } else {
            showMsg(getString(R.string.str_faild), null);
        }
    }

    // SerPower
    public void Button_SetPower(View v) {
        int setParam = _Max_Power;
        if (sp_Configration_Power != null) {
            setParam = Integer.parseInt(sp_Configration_Power.getSelectedItem().toString());
        }
        int antCount = 1;
        if (_NowAntennaNo == 3) {
            antCount = 2;
        }
        int rt = UHFReader._Config.SetANTPowerParam(antCount,setParam);
        if (rt==0) {
            showMsg(getString(R.string.str_success), null);
        } else {
            showMsg(getString(R.string.str_faild), null);
        }

    }

    // 读时间设置
    public void Button_SetPingPongReadTime(View v) {
        PublicData._PingPong_ReadTime = Integer.parseInt(sp_PingPongReadTime
                .getSelectedItem().toString());
        showMsg(getString(R.string.str_success), null);

    }

    // 间隔时间设置
    public void Button_SetPingPongStopTime(View v) {
        if (isFastClick()) {
            return;
        }
        PublicData._PingPong_StopTime = Integer.parseInt(sp_PingPongStopTime
                .getSelectedItem().toString());
        showMsg(getString(R.string.str_success), null);

    }

    // GetFrequency
    public void Button_GetFrequency(View v) {
        try {
            Integer frequencyNo = UHFReader._Config.GetFrequency();
            sp_Frequency.setSelection(frequencyNo);
            if (GetPower()) { // 频率功率一起查
                if (v != null) {
                    showMsg(getString(R.string.str_success), null);
                }
            } else {
                showMsg(getString(R.string.str_faild), null);
            }
        } catch (Exception ex) {
            if (v != null)
                showMsg(getString(R.string.str_faild), null);
        }


    }

    // SetFrequency
    public void Button_SetFrequency(View v) {
        if (isFastClick()) {
            return;
        }
        int param = sp_Frequency.getSelectedItemPosition();
        if (UHFReader._Config.SetFrequency(param)==0){
            Button_SetPower(v); // 频率功率一起改
            //showMsg(getString(R.string.str_success), null);
        } else {
            showMsg(getString(R.string.str_faild), null);
        }
    }

    // 恢复出厂设置
    public void Button_Configration_RF(View v) {
        if (isFastClick()) {
            return;
        }
        int rt = UHFReader._Config.SetReaderRestoreFactory();
        if (rt == 0) {
            edtRSSI.setText("101");
            int rssiValue = Integer.parseInt(edtRSSI.getText().toString().trim());
            PublicData.RSSI = rssiValue;
            getSharedPreferences("PreferenciasApp", MODE_PRIVATE).edit().putInt("RSSI", rssiValue).apply();

            showMsg(getString(R.string.str_success), null);
        } else {
            showMsg(getString(R.string.str_faild), null);
        }
    }

    // 设置读6C或6B标签
    public void Button_TagType_Set(View v) {
        if (isFastClick()) {
            return;
        }
        //
        PublicData._IsCommand6Cor6B = sp_TagType.getSelectedItem().toString();
        showMsg(getString(R.string.str_success), null);
    }

    // 升级基带
    public void Button_Configration_UpdateBase(View v) {
        super.showConfim("Confim UpdateBase？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                CLReader.UpdateSoft("");
            }
        }, null);
    }

    // GetBaseBand
    public void Button_GetBaseBand(View v) {
        if (isFastClick()) {
            return;
        }
        GetBaseBand();
    }

    @SuppressWarnings("serial")
    private void GetBaseBand() {
        //String rt = CLReader.GetBaseBand();
        String rt =UHFReader._Config.GetEPCBaseBandParam();
        String[] strParam = rt.split("\\|");
        if (strParam.length > 0) {
            for (int i = 0; i < strParam.length; i++) {
                if (i == 0) {
                    int iType = Integer.parseInt(strParam[i]);
                    if (iType == 255)
                        iType = 4;
                    sp_BaseSpeedType.setSelection(iType);
                } else if (i == 1) {

                    HashMap<String, Integer> mm = new HashMap<String, Integer>() {
                        {
                            put("4", 1);
                            put("0", 0);
                        }
                    };
                    if (mm.containsKey(strParam[i])) {
                        sp_BaseSpeedQValue.setSelection(mm.get(strParam[i]));
                    }
                }
            }
        }
    }

    // SetBaseBand
    public void Button_SetBaseBand(View v) {
        if (isFastClick()) {
            return;
        }
        int typePosition = sp_BaseSpeedType.getSelectedItemPosition();
        if (typePosition == 4)
            typePosition = 255;
        int qValue = Integer.parseInt(sp_BaseSpeedQValue.getSelectedItem()
                .toString());
        int rt = UHFReader._Config.SetEPCBaseBandParam(typePosition,qValue,0,2);
        if (rt==0) {
            showMsg(getString(R.string.str_success), null);
        } else {
            showMsg(getString(R.string.str_faild), null);
        }
    }

    public void Button_Test(View v) {
        if (isFastClick()) {
            return;
        }
        int antNo = Integer.parseInt(sp_Carrier.getSelectedItem().toString());
        UHFReader._Config.Set_0101_00((byte) antNo, (byte) 0x00);
        showMsg(UHFReader._Config.Get_0101_05(), null);
    }

}

