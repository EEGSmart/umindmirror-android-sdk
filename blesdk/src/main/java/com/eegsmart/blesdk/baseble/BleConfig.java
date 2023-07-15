package com.eegsmart.blesdk.baseble;

/**
 * 常量类
 * Created by Liusong on 2016/9/18
 * time 2016-05-13
 */
public class BleConfig {
    /** 支持蓝牙设备*/
    public static final int SUPPORT_BLE = 0;
    /** 设备不支持蓝牙 */
    public static final int NOT_SUPPORT_BLE = -1;
    /** 蓝牙初始化失败 */
    public static final int INIT_BLE_FAILED = -2;
    /** 蓝牙连接上 */
    public static final int BLE_CONNECT = 0x10;
    /** 蓝牙断开 */
    public static final int BLE_DISCONNECT = 0x11;

    public static final int NOTCH_60 = 60;
    public static final int NOTCH_50 = 50;
    public static int notchHz = NOTCH_60; // 默认60hz

    public static final float EEG_UV = 0.0554865056818182f;

}
