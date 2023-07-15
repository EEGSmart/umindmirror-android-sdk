package com.eegsmart.blesdk.model;

/**
 * Created by Liusong on 2017/7/19.
 */

public enum ControlType {
    /**
     * 未知
     */
    UNKNOWN(0x00),
    /**
     * 信号质量
     */
    POOR_SIGNAL_QUALITY(0x01),

    /**
     * 脑电数据
     */
    EEG_DATA(0x04),

    /**
     * 陀螺仪数据
     */
    GYRO_DATA(0x06),

    /**
     * 陀螺仪算法
     */
    GYRO_ALGO(0x07),

    /**
     * 50Hz滤波
     */
    FIR_FILTER(0x0f),

    /**
     * 时间同步
     */
    SYS_TIME(0x11),

    /**
     * 查询设备硬件版本
     */
    INQUIRE_DEVICE_HW_MSG(0x14),

    /**
     * 查询设备软件版本
     */
    INQUIRE_DEVICE_SW_MSG(0x15),

    /**
     * 查询设备SN
     */
    INQUIRE_DEVICE_SN_MSG(0x16),

    /**
     * 60Hz陷波
     */
    UPDATE_NOTCH(0x1A),

    /**
     * 组合控制开关
     */
    BATCH_CONTROL(0x20),

    /**
     * 关机信息
     */
    POWER_OFF(0x21),

    /**
     * 新电量信息
     */
    BATTERY_VALUE(0x3A),

    /**
     * 包序号
     */
    PAC_SEQ(0xF1),

    ;

    private int value;

    ControlType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ControlType getType(int type) {
        for (ControlType controlType : ControlType.values()) {
            if(controlType.getValue() == type){
                return controlType;
            }
        }
        return UNKNOWN;
    }
}
