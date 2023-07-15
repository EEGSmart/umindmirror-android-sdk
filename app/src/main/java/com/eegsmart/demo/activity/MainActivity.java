package com.eegsmart.demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.eegsmart.blesdk.baseble.AndroidBle;
import com.eegsmart.blesdk.baseble.BleConfig;
import com.eegsmart.blesdk.listener.OnConnectListener;
import com.eegsmart.blesdk.listener.OnDataListener;
import com.eegsmart.blesdk.model.BatteryStatus;
import com.eegsmart.blesdk.model.ControlType;
import com.eegsmart.blesdk.scanner.SleepDevice;
import com.eegsmart.blesdk.util.DataParseUtil;

import com.eegsmart.blesdk.util.OrderUtils;
import com.eegsmart.demo.util.PermissionUtils;
import com.eegsmart.demo.R;
import com.eegsmart.demo.view.LineChartView;

public class MainActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionUtils.checkPermissions(this);
        AndroidBle.getInstance().addOnConnectListener(onConnectListener);
        DataParseUtil.getInstance().addOnDataListener(OnDataListener);

        initView();
    }

    private LinearLayout llConnect;

    private TextView tvDeviceName;
    private TextView tvDeviceSn;
    private TextView tvSoftwareVersion;
    private TextView tvHardwareVersion;

    private LineChartView lcvEeg;

    private TextView tvSignalQuality;
    private TextView tvDeviceBattery;
    private TextView tvBatteryStatus;
    private TextView tvSignalRssi;
    private TextView tvBodyPosition;
    private TextView tvBodyMove;

    private RadioGroup rgNotch;

    private void initView() {
        tvDeviceName = findViewById(R.id.tvDeviceName);
        tvDeviceSn = findViewById(R.id.tvDeviceSn);
        tvSoftwareVersion = findViewById(R.id.tvSoftwareVersion);
        tvHardwareVersion = findViewById(R.id.tvHardwareVersion);
        lcvEeg = findViewById(R.id.lcvEeg);
        tvSignalQuality = findViewById(R.id.tvSignalQuality);
        tvDeviceBattery = findViewById(R.id.tvDeviceBattery);
        tvBatteryStatus = findViewById(R.id.tvBatteryStatus);
        tvSignalRssi = findViewById(R.id.tvSignalRssi);
        tvBodyPosition = findViewById(R.id.tvBodyPosition);
        tvBodyMove = findViewById(R.id.tvBodyMove);

        llConnect = findViewById(R.id.llConnect);
        llConnect.setOnClickListener(this);

        rgNotch = findViewById(R.id.rgNotch);
        rgNotch.check(BleConfig.notchHz == BleConfig.NOTCH_60 ? 2 : 1);
        rgNotch.setOnCheckedChangeListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llConnect:
                startActivity(new Intent(getContext(), ConnectDeviceActivity.class));
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()){
            case R.id.rgNotch:
                if(checkedId == 2){
                    OrderUtils.close50Hz();
                    OrderUtils.open60Hz();
                } else if (checkedId == 1){
                    OrderUtils.close60Hz();
                    OrderUtils.open50Hz();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidBle.getInstance().removeOnConnectListener(onConnectListener);
        DataParseUtil.getInstance().removeOnDataListener(OnDataListener);
        AndroidBle.getInstance().disConnect();
    }

    private void setTextOnUi(TextView textView, String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textView != null) textView.setText(text);
            }
        });
    }

    private OnConnectListener onConnectListener = new OnConnectListener() {

        @Override
        public void onConnectStatus(int status) {
            if (status == BleConfig.BLE_CONNECT) {
                SleepDevice sleepDevice = AndroidBle.getInstance().getConnectedDevice();
                setTextOnUi(tvDeviceName, sleepDevice.getName());
                toast(getString(R.string.device_connected));
            } else {
                setTextOnUi(tvDeviceName, "--");
                toast(getString(R.string.device_disconnected));
            }
        }

        @Override
        public void onRssi(int rssi) {
            setTextOnUi(tvSignalRssi, String.valueOf(rssi));
        }

    };

    private OnDataListener OnDataListener = new OnDataListener() {
        @Override
        public void onHardwareVersion(String version) {
            setTextOnUi(tvHardwareVersion, version);
        }

        @Override
        public void onDeviceVersion(String version) {
            setTextOnUi(tvSoftwareVersion, version);
        }

        @Override
        public void onDeviceSN(String sn) {
            setTextOnUi(tvDeviceSn, sn);
        }

        @Override
        public void onEegData(int[] data) {
            float[] dataUv = new float[data.length];
            for (int i = 0; i < dataUv.length; i++) {
                dataUv[i] = data[i] * BleConfig.EEG_UV;
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lcvEeg.addData(dataUv);
                }
            });
        }

        @Override
        public void onSignalQuality(int quality) {
            String noiseState = getString(R.string.signal_check); // 信号检测中
            if(quality == 0 || quality == 20) { // 信号良好
                noiseState = getString(R.string.signal_good);
            }else if (quality == 200){ // 信号不良
                noiseState = getString(R.string.signal_bad);
            }
            setTextOnUi(tvSignalQuality, noiseState);
            AndroidBle.getInstance().readRssi();
        }

        @Override
        public void onBattery(BatteryStatus batteryStatus, float batteryPercent, float voltage) {
           setTextOnUi(tvDeviceBattery, (int) batteryPercent + "%");
           String status = getString(R.string.battery_using);
           if(batteryStatus == BatteryStatus.CHARGING_NOT_FULL) {
               status = getString(R.string.battery_charging);
           }else if(batteryStatus == BatteryStatus.CHARGING_FULL) {
               status = getString(R.string.battery_full);
           }
           setTextOnUi(tvBatteryStatus, status);
        }

        @Override
        public void onBodyMove(String bodyPosition, int position, int move) {
            setTextOnUi(tvBodyPosition, bodyPosition + "(" + position + ")");
            setTextOnUi(tvBodyMove, "level " + move);
        }

    };


}
