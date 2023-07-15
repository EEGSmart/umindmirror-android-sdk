package com.eegsmart.blesdk.util;


import com.eegsmart.blesdk.baseble.AndroidBle;
import com.eegsmart.blesdk.baseble.BleConfig;
import com.eegsmart.blesdk.model.BatchOrder;
import com.eegsmart.blesdk.model.ControlType;
import com.eegsmart.blesdk.model.SendControlOrder;
import com.eegsmart.blesdk.model.SendOrder;
import com.eegsmart.blesdk.model.SwitchType;

import java.util.Calendar;
import java.util.Date;

/**
 * 向蓝牙发送指令
 * Created by Liusong on 2017/8/3.
 */

public class OrderUtils {

    /**
     * 打开滤波
     */
    public static void open50Hz() {
        SendControlOrder sendControlOrder = new SendControlOrder(ControlType.FIR_FILTER, SwitchType.SWITCH_ON);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(sendControlOrder.generateString()), true);
    }

    /**
     * 关闭滤波
     */
    public static void close50Hz() {
        SendControlOrder sendControlOrder = new SendControlOrder(ControlType.FIR_FILTER, SwitchType.SWITCH_OFF);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(sendControlOrder.generateString()), true);
    }

    /**
     * 获取硬件的软件版本信息
     */
    public static void getDeviceSoftVersion() {
        SendControlOrder sendControlOrder = new SendControlOrder(ControlType.INQUIRE_DEVICE_SW_MSG, SwitchType.SWITCH_ON);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(sendControlOrder.generateString()), true);
    }

    /**
     * 获取硬件的硬件版本信息
     */
    public static void getDeviceHardVersion() {
        SendControlOrder sendControlOrder = new SendControlOrder(ControlType.INQUIRE_DEVICE_HW_MSG, SwitchType.SWITCH_ON);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(sendControlOrder.generateString()), true);
    }

    /**
     * 查询设备SN
     */
    public static void getDeviceSN() {
        SendControlOrder sendControlOrder = new SendControlOrder(ControlType.INQUIRE_DEVICE_SN_MSG, SwitchType.SWITCH_OFF);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(sendControlOrder.generateString()), true);
    }

    /**
     * 组合开关
     */
    public static void openBatchControl() {
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(genBatchControl()), true);
    }

    /**
     * 设备关机
     */
    public static void shutdownDevice() {
        SendControlOrder sendControlOrder = new SendControlOrder(ControlType.POWER_OFF, SwitchType.SWITCH_ON);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(sendControlOrder.generateString()), true);
    }

    /**
     * 时间同步
     */
    public static void startSysTime() {
        int time = (int) (getTime() / 1000);
        byte[] times = IntToByte(time);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(generateTimeStr(times)), true);
    }

    /**
     * 打开60Hz滤波
     */
    public static void open60Hz() {
        SendControlOrder sendControlOrder = new SendControlOrder(ControlType.UPDATE_NOTCH, SwitchType.SWITCH_ON);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(sendControlOrder.generateString()), true);
    }

    /**
     * 关闭60HZ滤波
     */
    public static void close60Hz() {
        SendControlOrder sendControlOrder = new SendControlOrder(ControlType.UPDATE_NOTCH, SwitchType.SWITCH_OFF);
        AndroidBle.getInstance().sendValue(HexStringUtils.hexStringToBytes(sendControlOrder.generateString()), true);
    }

    private static long getTime() {
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        int offset = cal.get(Calendar.ZONE_OFFSET);
        cal.add(Calendar.MILLISECOND, -offset);
        long timeStampUTC = cal.getTimeInMillis();
        long timeStamp = date.getTime();
        float timeZone = (timeStamp - timeStampUTC) / (1000f * 3600);
        return (timeStamp + (long) (timeZone * 1000 * 3600));
    }

    /**
     * int转byte数组
     *
     * @param num
     * @return
     */
    private static byte[] IntToByte(long num) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (num & 0xff);
        bytes[1] = (byte) ((num >> 8) & 0xff);
        bytes[2] = (byte) ((num >> 16) & 0xff);
        bytes[3] = (byte) ((num >> 24) & 0xff);
        return bytes;
    }

    /**
     * 时间指令拼接
     *
     * @param times
     * @return
     */
    private static String generateTimeStr(byte[] times) {
        StringBuilder builder = new StringBuilder();
        int checkSum = (0x23 + 0x11 + 0x04);
        for (int i = 0; i < times.length; i++) {
            checkSum = checkSum + (times[i] & 0xff);
        }
        //拼接命令
        builder.append(SendOrder.formatToHexStr(0xaa));
        builder.append(SendOrder.formatToHexStr(0xaa));
        builder.append(SendOrder.formatToHexStr(0x00));
        builder.append(SendOrder.formatToHexStr(0x00));
        builder.append(SendOrder.formatToHexStr(0x07));
        builder.append(SendOrder.formatToHexStr(0x23));
        builder.append(SendOrder.formatToHexStr(0x11));
        builder.append(SendOrder.formatToHexStr(0x04));
        for (int i = 0; i < times.length; i++) {
            builder.append(SendOrder.formatToHexStr((times[i] & 0xff)));
        }
        checkSum = 0xff - (checkSum & 0xff);
        builder.append(SendOrder.formatToHexStr(checkSum));

        return builder.toString();
    }

    /**
     * 组合开关指令拼接
     *
     * @return
     */
    private static String genBatchControl() {
        BatchOrder batchOrder = new BatchOrder();
        batchOrder.EEG_ALGO = true;
        batchOrder.EEG_DATA = true;

        batchOrder.GYRO_ALGO = true;
        batchOrder.GYRO_DATA = true;

        batchOrder.HR_SPO2_ALGO = true;
        batchOrder.HR_SPO2_DATA = true;

        batchOrder.MIC_ALGO = true;

        batchOrder.BATTERY_VAL_DATA = true;
        batchOrder.BODY_TEMP_DATA = true;

        batchOrder.NOTCH_60HZ_FILTER = BleConfig.notchHz == BleConfig.NOTCH_60;
        batchOrder.FIR_FILTER = BleConfig.notchHz == BleConfig.NOTCH_50;

        String controlStr = batchOrder.getValue1();

        StringBuilder builder = new StringBuilder();
        long data = Long.valueOf(controlStr, 2);
        byte[] order = IntToByte(data);
        //拼接命令
        builder.append(SendOrder.formatToHexStr(0xaa));
        builder.append(SendOrder.formatToHexStr(0xaa));
        builder.append(SendOrder.formatToHexStr(0x00));
        builder.append(SendOrder.formatToHexStr(0x00));
        builder.append(SendOrder.formatToHexStr(0x08));
        builder.append(SendOrder.formatToHexStr(0x22));//开关类型
        builder.append(SendOrder.formatToHexStr(ControlType.BATCH_CONTROL.getValue()));//指令类型
        builder.append(SendOrder.formatToHexStr(0x05));//数据长度
        for (int i = 0; i < order.length; i++) {
            builder.append(SendOrder.formatToHexStr((order[i] & 0xff)));
        }

        int checkSum = (0x22 + 0x20 + 0x05);
        for (int i = 0; i < order.length; i++) {
            checkSum = checkSum + (order[i] & 0xff);
        }
        checkSum = 0xff - (checkSum & 0xff);
        builder.append(SendOrder.formatToHexStr(0x00));
        builder.append(SendOrder.formatToHexStr(checkSum));

        return builder.toString();
    }

}
