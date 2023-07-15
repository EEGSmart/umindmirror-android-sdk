package com.eegsmart.blesdk.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.text.TextUtils;

import com.eegsmart.blesdk.baseble.AndroidBle;
import com.eegsmart.blesdk.util.HexStringUtils;
import com.eegsmart.blesdk.util.ScanRecordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 蓝牙搜索器
 *
 * @see AndroidBle
 * @see SleepDevice 硬件描述类
 */
public final class Scanner702 {

    private static final String TAG = "Scanner702";
    private static final long SCAN_PERIOD = 15000;  // 搜索时长
    private static String[] names = new String[]{ // 通过名称过滤搜索的设备
            "umind", "cateye", "smmy"
    };

    /**
     * 状态和搜索结果监听器
     */
    public interface Listener {
        /**
         * @param list 搜索结果列表  客户端应该直接使用整个list  replace(list)
         */
        void onDeviceListUpdated(ArrayList<SleepDevice> list);

        /**
         * @param scan 是否在搜索
         */
        void onScanning(boolean scan);
    }

    private BluetoothAdapter mBtAdapter;
    private Handler handler;
    private ArrayList<SleepDevice> deviceList;
    private List<Listener> listeners;
    private NotifyScanListThread notifyScanListThread;

    private boolean scanning;

    public Scanner702() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = new ArrayList<>();
        listeners = new ArrayList<>();
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * @param listener 添加监听器
     */
    public void addListener(Listener listener) {
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    /**
     * @param listener 剔除指定监听器
     */
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    /**
     * 清除所有监听器  通常在退出应用或界面时调用
     */
    public void clearListener() {
        listeners.clear();
    }

    /**
     * @return 当前是否在搜索
     */
    public boolean isScanning() {
        return scanning;
    }


    private Runnable stopScanLeRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                stopNotifyThread();
                scanning = false;
                notifyScanStatus(false);
                mBtAdapter.stopLeScan(mLeScanCallback);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };

    private void restartNotifyThread() {
        stopNotifyThread();
        notifyScanListThread = new NotifyScanListThread(this);
        notifyScanListThread.start();
    }

    private void stopNotifyThread() {
        if (null != notifyScanListThread) {
            notifyScanListThread.interrupt();
            notifyScanListThread = null;
        }
    }

    /**
     * 启动搜索  会获取已配对的设备
     */
    public void startScan() {
        deviceList.clear();
        handler.removeCallbacks(stopScanLeRunnable);
        handler.postDelayed(stopScanLeRunnable, SCAN_PERIOD);
        scanning = true;
        notifyScanStatus(scanning);
        restartNotifyThread();
        mBtAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * 停止搜索
     */
    public void stopScan() {
        stopNotifyThread();
        handler.removeCallbacks(null);
        scanning = false;
        notifyScanStatus(false);
        mBtAdapter.stopLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String name = device.getName();
            if (TextUtils.isEmpty(name) ) {
                return;
            }

            boolean isContains = false;
            for (int i = 0; i < names.length; i++) {
                if(name.toLowerCase().contains(names[i].toLowerCase())){
                    isContains = true;
                    break;
                }
            }
            if(!isContains){
                return;
            }

            SleepDevice currentDevice = AndroidBle.getInstance().getConnectedDevice();

            if (currentDevice == null || !currentDevice.getAddress().equals(device.getAddress())) {
                SleepDevice item = new SleepDevice(device);
                item.setRssi(rssi);
                int index = deviceList.indexOf(item);
                if (index <= -1) {
                    deviceList.add(item);
                    parseRecord(item, scanRecord);
                } else {
                    deviceList.get(index).setRssi(rssi);
                }
            }
        }
    };

    private void parseRecord(SleepDevice item, byte[] scanRecord){
        // 解析广播
        ScanRecordUtil scanRecordUtil = ScanRecordUtil.parseFromBytes(scanRecord);
        Map<ParcelUuid, byte[]> map = scanRecordUtil.getServiceData();
        if(map != null){
            int battery = 0;
            for (ParcelUuid parcelUuid : map.keySet()) {
                String uuid = parcelUuid.getUuid().toString();
                if(uuid.startsWith("0000fff5")){ // SN
                    byte[] baSn = map.get(parcelUuid);
                    String sn = HexStringUtils.toStringHex1(HexStringUtils.bytesToHexString(baSn));
                    item.setSn(sn);
                }

                if(uuid.startsWith("0000180f")){ // 新电量
                    byte[] baBattery = map.get(parcelUuid);
                    if(baBattery != null && baBattery.length > 0){
                        battery = baBattery[0];
                    }
                }
            }

            if(battery > 0){
                item.setBattery(battery);
            }
            if(item.getSn().equals("--")){
                item.setSn(item.getAddress());
            }
        }
    }

    private void notifyDeviceListChanged(final ArrayList<SleepDevice> deviceList) {
        for (Listener l : listeners) {
            l.onDeviceListUpdated(deviceList);
        }
    }

    private void notifyScanStatus(boolean scanning) {
        final ArrayList<Listener> list = new ArrayList<>(listeners);
        for (Listener l : list) {
            l.onScanning(scanning);
        }
    }

    private static class NotifyScanListThread extends Thread {
        Scanner702 mmScanner;

        public NotifyScanListThread(Scanner702 s) {
            this.mmScanner = s;
        }

        @Override
        public void run() {
            super.run();
            while (mmScanner.isScanning() && !isInterrupted()) {
                try {
                    Thread.sleep(2000L);
                    mmScanner.notifyDeviceListChanged(mmScanner.deviceList);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }
}
