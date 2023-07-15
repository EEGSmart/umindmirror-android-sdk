package com.eegsmart.blesdk.listener;

import com.eegsmart.blesdk.model.BatteryStatus;
import com.eegsmart.blesdk.model.ControlType;

/**
 * 监听接收设备的数据
 * Created by Liusong on 2017/8/3.
 */

public abstract class OnDataListener {

    /**
     * 信号质量
     *
     * @param value 信号值
     */
    public void onSignalQuality(int value){}

    /**
     * 脑电数据
     *
     * @param data 脑电数据
     */
    public void onEegData(int[] data){}

    /**
     * 陀螺仪算法数据
     *
     * @param bodyPosition   体位
     * @param position       体位
     * @param bodyMoveDegree 体动等级
     */
    public void onBodyMove(String bodyPosition, int position, int bodyMoveDegree){}

    /**
     * 电池电压数据
     *
     * @param isCharging     是否在充电中
     * @param batteryPercent 电量百分比
     * @param voltage        电量电压
     */
    public void onBattery(BatteryStatus isCharging, float batteryPercent, float voltage){}

    /**
     * 监听某项数据有没有打开
     *
     * @param type   哪一项数据
     * @param status 0代表打开成功，1代表打开失败
     */
    public void onDataOpen(ControlType type, int status){}

    /**
     * 获取设备的软件版本信息
     *
     * @param version 版本信息
     */
    public void onDeviceVersion(String version){}

    /**
     * 获取设备的硬件版本信息
     *
     * @param version 版本信息
     */
    public void onHardwareVersion(String version){}

    /**
     * 获取设备的序列号
     *
     * @param SNInfo 序列号
     */
    public void onDeviceSN(String SNInfo){}

    /**
     * 关机信息
     *
     * @param reason 关机原因
     */
    public void onPowerOff(int reason){}

    /**
     * 数据是否连接
     *
     * @param isConnect 是否连接
     */
    public void onDataConnect(boolean isConnect){}

}
