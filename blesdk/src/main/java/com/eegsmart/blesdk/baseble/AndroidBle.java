package com.eegsmart.blesdk.baseble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.eegsmart.blesdk.check.CheckOrderUtils;
import com.eegsmart.blesdk.listener.OnConnectListener;
import com.eegsmart.blesdk.check.OrderState;
import com.eegsmart.blesdk.scanner.SleepDevice;
import com.eegsmart.blesdk.util.DataParseUtil;
import com.eegsmart.blesdk.util.HexStringUtils;
import com.eegsmart.blesdk.util.UtilSharedPreference;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Liusong on 2017/08/01
 * time 2016-08-01
 */
public class AndroidBle {
    public final String TAG = getClass().getSimpleName();

    /** 写数据的特征值*/
    public String WRITE_CHARACTER_UUID = "0000fff2-0000-1000-8000-00805f9b34fb";
    /**  激活通知的特征值*/
    public String NOTIFY_CHARACTER_UUID = "0000fff1-0000-1000-8000-00805f9b34fb";
    public final String DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    public String DEVICE_ADDRESS = "device_address";

    // 不变
    private BluetoothAdapter mBtAdapter;
    private Context context;
    private BluetoothGattCharacteristic writeCh;
    private BluetoothGatt mBluetoothGatt;
    private SleepDevice sleepDevice = null;

    public static AndroidBle getInstance() {
        return AndroidBleHolder.mInstance;
    }
    private static class AndroidBleHolder {
        private static final AndroidBle mInstance = new AndroidBle();
    }

    /**
     * 初始化蓝牙
     * @param context 上下文
     * @return 初始化结果(0:支持蓝牙; -1:不支持蓝牙; -2:初始化失败)
     */
    public int initBleClient(Context context) {
        this.context = context.getApplicationContext();
        int status = BleConfig.NOT_SUPPORT_BLE;

        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return status;
        }
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager == null){
            status = BleConfig.INIT_BLE_FAILED;
            return status;
        }

        mBtAdapter = bluetoothManager.getAdapter();
        if (mBtAdapter == null) {
            status = BleConfig.INIT_BLE_FAILED;
            return status;
        }
        status = BleConfig.SUPPORT_BLE;

        return status;
    }

    /**
     * 获取蓝牙代理
     * @return
     */
    public BluetoothAdapter getAdapter() {
        if(mBtAdapter == null && context != null){
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if(bluetoothManager != null){
                mBtAdapter = bluetoothManager.getAdapter();
            }
        }
        return mBtAdapter;
    }

    /**
     * 连接设备
     * @param address 设备地址
     * @return
     */
    public boolean connect(String address) {
        mBtAdapter = getAdapter();

        if(mBtAdapter == null){
            Log.i(TAG, "mBtAdapter null");
            return false;
        }

        if(!mBtAdapter.isEnabled()) {
            Log.i(TAG, "mBtAdapter close");
            return false;
        }

        if(address.isEmpty()){
            Log.i(TAG, "address null");
            return false;
        }

        BluetoothDevice remoteDevice = mBtAdapter.getRemoteDevice(address);
        if (remoteDevice == null) {
            Log.i(TAG, "remoteDevice null");
            return false;
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
        }
        Log.i(TAG, "connect " + address + " " + connectRetry);
        mBluetoothGatt = remoteDevice.connectGatt(context, false, mGattcallback);

        UtilSharedPreference.saveString(context, address, remoteDevice.getName());
        return true;
    }

    private List<byte[]> listValue = new LinkedList<>();
    private boolean isWriting = false;
    public synchronized boolean sendValue(byte[] bytes, boolean isWrite) {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "mBluetoothGatt == null");
            return false;
        }
        if (writeCh == null) {
            Log.e(TAG, "writeCh == null");
            return false;
        }

        if (isWrite) {
            boolean bSend = false;
            if(bytes.length > 0) {
                listValue.add(bytes);
            }
            if(!listValue.isEmpty() && !isWriting){
                byte[] bytesFirst = listValue.get(0);
                writeCh.setValue(bytesFirst);
                bSend = mBluetoothGatt.writeCharacteristic(writeCh);
                Log.d(TAG, "cmd write:" + HexStringUtils.bytes2String(bytesFirst) + " result:" + bSend);
                if(bSend) isWriting = true;
            }
            return bSend;
        } else {
            return mBluetoothGatt.readCharacteristic(writeCh);
        }
    }

    /**
     * 断开当前连接
     */
    public void disConnect() {
        if (mBtAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
        cleanBleCache();
        mBluetoothGatt = null;
    }

    /**
     * 获取已连接设备
     * @return 已连接设备（null为未连接）
     */
    public SleepDevice getConnectedDevice() {
        return sleepDevice;
    }

    /**
     * 设置已连接设备
     * @param mConnectedDevice 蓝牙设备
     */
    private void setConnectedDevice(BluetoothDevice mConnectedDevice) {
        if(mConnectedDevice == null){
            sleepDevice = null;
        }else{
            sleepDevice = new SleepDevice();
            String address = mConnectedDevice.getAddress();
            sleepDevice.setAddress(address);
            String name = mConnectedDevice.getName();
            // 有时设备没有名称, 使用之前连接时获取的名称
            if(name == null || name.isEmpty()){
                name = UtilSharedPreference.getStringValue(context, address, "");
            }
            sleepDevice.setName(name);
        }
    }

    /**
     * 重置设备数据
     */
    private void resetConnect(){
        connectRetry = 0;
        listValue.clear();
        isWriting = false;
        DataParseUtil.getInstance().resetDataList();
    }

    private int connectRetry = 0; // 重连计数
    private BluetoothGattCallback mGattcallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "status:" + status + ",newState:" + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 重置数据
                resetConnect();
                setConnectedDevice(gatt.getDevice());
                notifyConnectionState(gatt, BleConfig.BLE_CONNECT);

                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // 清除数据
                gatt.close();
                cleanBleCache();
                setConnectedDevice(null);
                CheckOrderUtils.destroyHandler();
                notifyConnectionState(gatt, BleConfig.BLE_DISCONNECT);
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
            List<BluetoothGattService> serverList = gatt.getServices();
            for (final BluetoothGattService server : serverList) {
                Log.d(TAG, "service:" + server.getUuid().toString());
                List<BluetoothGattCharacteristic> characteristics = server.getCharacteristics();
                for (final BluetoothGattCharacteristic characteristic : characteristics) {
                    boolean isWrite = WRITE_CHARACTER_UUID.equals(characteristic.getUuid().toString());
                    boolean isNotify = NOTIFY_CHARACTER_UUID.equals(characteristic.getUuid().toString());
                    Log.d(TAG, "characteristic:" + characteristic.getUuid().toString() + " " + isWrite + " " + isNotify);
                    if (isWrite) {
                        Log.e(TAG, "write " + characteristic.getUuid().toString());
                        writeCh = characteristic;
                    }
                    if (isNotify) {
                        Log.e(TAG, "notify " + characteristic.getUuid().toString());
                        int properties = characteristic.getProperties();  //返回特征值的属性
                        if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {  //判断是否具有通知属性
                            mBluetoothGatt.setCharacteristicNotification(characteristic, true);
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(DESCRIPTOR_UUID));
                            if (descriptor != null) {
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                mBluetoothGatt.writeDescriptor(descriptor);
                                Log.e(TAG, "enable notify");
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            // 打开通知后发送数据
            CheckOrderUtils.setTimerOrder(OrderState.Type.GET_HD_VERSION);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, final int status) {
            byte[] value = characteristic.getValue();
            byte[] bytes = listValue.get(0);
            if(value == null || value.length == 0 || bytes.length == 0 || bytes == characteristic.getValue()){
                listValue.remove(0);
            }
            isWriting = false;
            sendValue(new byte[0], true);
        }

        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] bleInputByte = characteristic.getValue();
            final byte[] bleValue = new byte[bleInputByte.length]; // 将数组数据复制一份
            System.arraycopy(bleInputByte, 0, bleValue, 0, bleInputByte.length);
            DataParseUtil.getInstance().dealValue(bleValue);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            SleepDevice sleepDevice = getConnectedDevice();
            if(sleepDevice != null)
                sleepDevice.setRssi(rssi);
            setOnRssiCallback(rssi);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.i(TAG, "onMtuChanged " + mtu + " " + status);
        }
    };

    /**
     * 通知监听器连接状态改变
     */
    private void notifyConnectionState(BluetoothGatt gatt, int bleConnCode) {
        if (null != onConnectListeners) {
            for (OnConnectListener l : onConnectListeners) {
                l.onConnectStatus(bleConnCode);
            }
        }
    }

    /**
     * 清除蓝牙的缓存
     */
    public void cleanBleCache() {
        listValue.clear();

        if (mBluetoothGatt != null) {
            try {
                BluetoothGatt localBluetoothGatt = mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                }
            } catch (Exception localException) {
                Log.e(TAG, "An exception occured while refreshing device");
            }
        }
    }

    private List<OnConnectListener> onConnectListeners = new ArrayList<>();

    public void addOnConnectListener(OnConnectListener onConnectListener) {
        if (null != onConnectListener) {
            if (!this.onConnectListeners.contains(onConnectListener)) {
                this.onConnectListeners.add(onConnectListener);
            }
        }
    }

    public void removeOnConnectListener(OnConnectListener onConnectListener) {
        if (null != onConnectListener) {
            if (this.onConnectListeners.contains(onConnectListener)) {
                this.onConnectListeners.remove(onConnectListener);
            }
        }
    }

    /**
     * 读取rssi
     */
    public void readRssi() {
        if (null != mBluetoothGatt) {
            mBluetoothGatt.readRemoteRssi();
        }
    }

    private void setOnRssiCallback(int rssi) {
        for (OnConnectListener onConnectListener : onConnectListeners) {
            onConnectListener.onRssi(rssi);
        }
    }
}
