package com.eegsmart.demo.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eegsmart.blesdk.baseble.AndroidBle;
import com.eegsmart.blesdk.baseble.BleConfig;
import com.eegsmart.blesdk.listener.OnConnectListener;
import com.eegsmart.blesdk.scanner.Scanner702;
import com.eegsmart.blesdk.scanner.SleepDevice;

import java.util.ArrayList;

import com.eegsmart.demo.util.DeviceListAdapter;
import com.eegsmart.demo.R;

public class ConnectDeviceActivity extends BaseActivity implements View.OnClickListener {

    private ListView devicesListView;
    private ImageView scanImageButton;
    private View connectedLayout;
    private TextView nameTv;
    private RelativeLayout btLayout;
    private TextView disconnectTv;

    private static ArrayList<SleepDevice> mDevicesList = new ArrayList<>();
    private static DeviceListAdapter deviceListAdapter;

    private Animation mRefreshAnim;
    private Scanner702 scanner = new Scanner702();
    private int mClickItemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_device);
        initView();
        initData();
    }

    private ProgressDialog progressDialog;
    private void initView() {
        devicesListView = findViewById(R.id.devicesListView);
        scanImageButton = findViewById(R.id.scanImageButton);
        connectedLayout = findViewById(R.id.connectedLayout);
        nameTv = findViewById(R.id.nameTv);
        btLayout = findViewById(R.id.btLayout);
        disconnectTv = findViewById(R.id.disconnectTv);

        disconnectTv.setOnClickListener(this);
        scanImageButton.setOnClickListener(this);
        btLayout.setOnClickListener(this);

        mRefreshAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.refresh_anim);
        LinearInterpolator lin = new LinearInterpolator();
        mRefreshAnim.setInterpolator(lin);

        deviceListAdapter = new DeviceListAdapter(this.getLayoutInflater());
        devicesListView.setAdapter(deviceListAdapter);
        devicesListView.setOnItemClickListener(mDeviceClickListener);

        progressDialog = new ProgressDialog(getContext());
    }

    private void initData() {
        AndroidBle.getInstance().addOnConnectListener(onConnectListener);
        scanner.addListener(listener);
        connectedLayoutState();
        updateBtView();
        scanner.startScan();
    }

    /**
     * 蓝牙搜索
     */
    private void startScan() {
        if (!AndroidBle.getInstance().getAdapter().isEnabled()) {
            return;
        }
        deviceListAdapter.clear();
        deviceListAdapter.notifyDataSetChanged();
        scanner.stopScan();
        scanner.startScan();
    }

    /**
     * 蓝牙连接状态对应的UI显示
     */
    private void connectedLayoutState() {
        SleepDevice device = AndroidBle.getInstance().getConnectedDevice();
        if (null != device) {
            connectedLayout.setVisibility(View.VISIBLE);
            nameTv.setText(device.getName());
        } else {
            connectedLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AndroidBle.getInstance().removeOnConnectListener(onConnectListener);
        scanner.removeListener(listener);
        scanner.stopScan();
        progressDialog.dismiss();
    }

    /**
     * 蓝牙操控条显示
     */
    private void updateBtView() {
        if (AndroidBle.getInstance().getAdapter().isEnabled()) {
            btLayout.setVisibility(View.GONE);
        } else {
            btLayout.setVisibility(View.VISIBLE);
        }
    }

    private Scanner702.Listener listener = new Scanner702.Listener() {
        @Override
        public void onDeviceListUpdated(ArrayList<SleepDevice> list) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDevicesList = new ArrayList<>(list);
                    deviceListAdapter.addDevice(mDevicesList);
                }
            });
        }

        @Override
        public void onScanning(boolean scan) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(scan){
                        scanImageButton.startAnimation(mRefreshAnim);
                    }else{
                        scanImageButton.clearAnimation();
                    }
                }
            });
        }
    };

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long arg3) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.show();
                    scanner.stopScan();
                    mClickItemPosition = position;
                    SleepDevice sleepDevice = deviceListAdapter.getDevice(position);
                    AndroidBle.getInstance().disConnect();
                    AndroidBle.getInstance().connect(sleepDevice.getAddress());
                }
            });
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.disconnectTv:
                new AlertDialog.Builder(ConnectDeviceActivity.this).setMessage(getText(R.string.confirm_to_disconnect_drone))
                        .setCancelable(true)
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.show();
                                AndroidBle.getInstance().disConnect();
                                connectedLayoutState();
                            }
                        }).create().show();
                break;
            case R.id.scanImageButton:
                startScan();
                break;
            case R.id.btLayout:
                AndroidBle.getInstance().getAdapter().enable();
                btLayout.setVisibility(View.GONE);
                break;
        }
    }

    private OnConnectListener onConnectListener = new OnConnectListener() {
        @Override
        public void onConnectStatus(final int statue) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.hide();

                    if (statue == BleConfig.BLE_CONNECT) {
                        deviceListAdapter.removeDevice(mClickItemPosition);
                        deviceListAdapter.notifyDataSetChanged();
                    }

                    connectedLayoutState();
                }
            });
        }

        @Override
        public void onRssi(int rssi) {
            super.onRssi(rssi);
        }

    };


}