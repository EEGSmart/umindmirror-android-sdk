package com.eegsmart.blesdk.listener;

/**
 * 蓝牙连接状态的回调接口
 * Created by Liusong on 2016/9/19
 */
public abstract class OnConnectListener {
    /**
     * 设备连接状态 (此接口设备连接成功不一定就 可以进行通讯)
     *
     * @param statue  (BleConfig.BLE_CONNECT:连接成功；BleConfig.BLE_DISCONNECT:断开连接)
     */
    public void onConnectStatus(int statue){};

    /**
     * 设备的rssi值
     * @param rssi
     */
    public void onRssi(int rssi){};

}
