package com.eegsmart.blesdk.util;

import android.util.Log;

import com.eegsmart.blesdk.baseble.AndroidBle;
import com.eegsmart.blesdk.bean.BufferStructure;
import com.eegsmart.blesdk.check.CheckOrderUtils;
import com.eegsmart.blesdk.listener.OnDataListener;
import com.eegsmart.blesdk.model.BatteryStatus;
import com.eegsmart.blesdk.model.ClassType;
import com.eegsmart.blesdk.model.ControlType;
import com.eegsmart.blesdk.check.OrderState;
import com.eegsmart.blesdk.scanner.SleepDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 数据解析类
 * Created by Liusong on 2017/7/20.
 */

public class DataParseUtil {

    private final static String TAG = DataParseUtil.class.getSimpleName();
    private CopyOnWriteArrayList<Byte> DataList = new CopyOnWriteArrayList<>();
    private static final int MIN_PACKAGE_UNIT_LENGTH = 8;
    private BatteryStatus batteryStatus; // 当前电池状态

    private DataParseUtil() {
    }

    public static DataParseUtil getInstance() {
        return ParseUtilHolder.mInstance;
    }

    private static class ParseUtilHolder {
        private static final DataParseUtil mInstance = new DataParseUtil();
    }

    public void resetDataList() {
        batteryStatus = null;  // 清除电池状态信息
        DataList.clear();
    }

    public void dealValue(byte[] values) {
        for (int i = 0; i < values.length; i++) {
            DataList.add(values[i]);
        }
        for (int k = 0; k < DataList.size() && DataList.size() >= MIN_PACKAGE_UNIT_LENGTH; k++) {
            if (DataList.get(0) == intToByte(0xaa) && DataList.get(1) == intToByte(0xaa)) {
                int packLen = byteToInt(DataList.get(4));  //取出数据的长度
                if (packLen + 6 <= DataList.size()) {
                    final byte[] buffer = new byte[packLen + 6];
                    for (int j = 0; j < buffer.length; j++) {
                        buffer[j] = DataList.remove(0);
                    }
                    if (checkSum(buffer)) {
//                        Log.d(TAG, "校验成功:" + HexStringUtils.bytesToHexString(buffer));
                        //开始解析
                        startParseBuffer(buffer);
                    } else {
                        Log.e(TAG, "校验不成功:" + HexStringUtils.bytes2String(buffer));
                    }
                }
            } else {
                DataList.remove(0);
            }
        }
    }

    /**
     * 解析一条完整的buffer
     *
     * @param buffer
     */
    public void startParseBuffer(byte[] buffer) {
        if (buffer.length > 7) {
            int classType = byteToInt(buffer[5]);   //拿出classType类型
            List<BufferStructure> bufferStructures = dataSeparation(classType, buffer);
            for (BufferStructure bu : bufferStructures) {
                ClassType type = ClassType.getType(bu.getClassType());
                switch (type) {
                    case CONTROL_SWITCH:   //返回开关的状态（0x22）,数据：data + 应答（00开，01关）
                        parseSwitch(bu);
                        break;
                    case CONTROL_DATA:  //返回的数据
                        bu.setRaw(buffer);
                        parseData(bu);
                        break;
                }
            }
        } else {
            Log.e(TAG, "包有问题");
        }
    }

    /**
     * 把一条完整的buff分解成一组一组的数据，每组数据都带classType
     * 从第五位开始循环解析(i=3)，一直到倒数第二位(例:aaaa0522 04020001 d6,解析中间的数据)
     *
     * @param buffer
     */
    private List<BufferStructure> dataSeparation(int classType, byte[] buffer) {
        List<BufferStructure> list = new ArrayList<>();
        for (int i = 6; i < buffer.length - 1; ) {
            int head = byteToInt(buffer[i++]);  //head，代表是什么数据
            int len = byteToInt(buffer[i++]);   //获取数据长度len
            try {
                byte[] data = new byte[len];
                System.arraycopy(buffer, i, data, 0, len);   //数组拷贝,获取数据的数组
                i += len;
                BufferStructure buff = new BufferStructure(classType, head, len, data);
                list.add(buff);
            } catch (IndexOutOfBoundsException e) {
                Log.e(TAG, "Data parse error! " + e);
            }
        }
        return list;
    }

    /**
     * 解析开关控制（初始化开关状态）
     *
     * @param bu
     */
    private void parseSwitch(BufferStructure bu) {
        ControlType type = ControlType.getType(bu.getHead());
        byte[] data = bu.getData();
//        Log.d(TAG, "parseSwitch [" + type + "] " + HexStringUtils.bytesToHexString(data));
        if (data.length > 0 && byteToInt(data[data.length - 1]) == 0x01) {  //01代表收到的数据
            for (OnDataListener l : onDataListeners) {
                l.onDataOpen(type, byteToInt(data[0]));
            }
        }
        switch (type) {
            case INQUIRE_DEVICE_HW_MSG:
                CheckOrderUtils.setTimerOrder(OrderState.Type.GET_SN_NUMBER);// 硬件版本 -> SN
                break;
            case INQUIRE_DEVICE_SN_MSG:
                CheckOrderUtils.setTimerOrder(OrderState.Type.GET_DEV_SW_MSG);// SN -> 软件版本
                break;
            case INQUIRE_DEVICE_SW_MSG:
                CheckOrderUtils.setTimerOrder(OrderState.Type.GET_SYS_TIME);// 软件版本 -> 同步时间
                break;
            case BATCH_CONTROL:
                CheckOrderUtils.destroyHandler();//销毁order Timer
                if (null != onDataListeners) {
                    for (OnDataListener l : onDataListeners) {
                        l.onDataConnect(true);
                    }
                }
                break;
        }
    }

    /**
     * 解析数据
     *
     * @param bu 数据
     */
    private void parseData(BufferStructure bu) {
        SleepDevice sleepDevice = AndroidBle.getInstance().getConnectedDevice();

        ControlType type = ControlType.getType(bu.getHead());
        byte[] data = bu.getData();
        switch (type) {
            case SYS_TIME:
                CheckOrderUtils.setTimerOrder(OrderState.Type.GET_SYS_OPEN_DATE);// 同步时间 -> 打开功能
                break;
            case POOR_SIGNAL_QUALITY:
                if (data.length == 1) {
                    int signal = byteToInt(data[0]);

                    if(sleepDevice != null){
                        sleepDevice.setWear(signal);
                    }

                    for (OnDataListener l : onDataListeners) {
                        l.onSignalQuality(signal);
                    }
                }
                break;
            case EEG_DATA:
                if (data.length > 1) {
                    int[] eegDataOld = twoBytestoInt(data);
                    int[] eegDataNew = new int[eegDataOld.length];
                    for (int i = 0; i < eegDataOld.length; i++) {
                        eegDataNew[i] = eegDataOld[i] - 0x2000;
                    }
                    if (null != onDataListeners) {
                        for (OnDataListener l : onDataListeners) {
                            l.onEegData(eegDataNew);
                        }
                    }
                }
                break;
            case GYRO_ALGO:  //陀螺仪算法打开,陀螺仪按一个byte去解析
                if (data.length == 2) {
                    int bodyPosi = DataParseUtil.byteToInt(data[0]);  //体位
                    int bodyMoveDegree = DataParseUtil.byteToInt(data[1]);  //体动等级
                    String position = "stand";
                    if (bodyPosi == 0) {
                        position = "none";
                    } else if (bodyPosi == 1) {
                        position = "back";
                    } else if (bodyPosi == 2) {
                        position = "left";
                    } else if (bodyPosi == 3) {
                        position = "front";
                    } else if (bodyPosi == 4) {
                        position = "right";
                    } else if (bodyPosi == 5) {
                        position = "stand";
                    } else if (bodyPosi == 6) {
                        position = "down";
                    } else if (bodyPosi == 7) {
                        position = "walk";
                    }
                    if (null != onDataListeners) {
                        for (OnDataListener l : onDataListeners) {
                            l.onBodyMove(position, bodyPosi, bodyMoveDegree);
                        }
                    }
                }
                break;
            case BATTERY_VALUE:
                if(data.length >= 4){
                    // 充电状态
                    if(data[0] == 2)
                        batteryStatus = BatteryStatus.CHARGING_FULL;
                    else if(data[0] == 1)
                        batteryStatus = BatteryStatus.CHARGING_NOT_FULL;
                    else
                        batteryStatus = BatteryStatus.USING;
                    // 电量
                    float batteryPercent = data[1];
                    // 电压
                    int[] batteryData = twoBytestoInt(data);
                    int battery = batteryData[1];
                    float voltage = battery * 0.001f;

                    if(sleepDevice != null){
                        sleepDevice.setBattery(batteryPercent);
                        sleepDevice.setChargeState(batteryStatus);
                        sleepDevice.setVoltage(voltage);
                    }

                    if (null != onDataListeners) {
                        for (OnDataListener l : onDataListeners) {
                            l.onBattery(batteryStatus, batteryPercent, voltage);
                        }
                    }
                }
                break;
            case INQUIRE_DEVICE_HW_MSG:
                String versionHard = HexStringUtils.toStringHex1(HexStringUtils.bytesToHexString(data));
                AndroidBle.getInstance().getConnectedDevice().setVersionHard(versionHard);
                if (null != onDataListeners) {
                    for (OnDataListener l : onDataListeners) {
                        l.onHardwareVersion(versionHard);
                    }
                }
                break;
            case INQUIRE_DEVICE_SW_MSG:
                String versionSoft = HexStringUtils.toStringHex1(HexStringUtils.bytesToHexString(data));
                AndroidBle.getInstance().getConnectedDevice().setVersionSoft(versionSoft);
                if (null != onDataListeners) {
                    for (OnDataListener l : onDataListeners) {
                        l.onDeviceVersion(versionSoft);
                    }
                }
                break;
            case INQUIRE_DEVICE_SN_MSG:
                String sn = HexStringUtils.toStringHex1(HexStringUtils.bytesToHexString(data));
                AndroidBle.getInstance().getConnectedDevice().setSn(sn);
                if (null != onDataListeners) {
                    for (OnDataListener l : onDataListeners) {
                        l.onDeviceSN(sn);
                    }
                }
                break;
            case POWER_OFF:
                if(data.length <= 2){
                    if (null != onDataListeners) {
                        for (OnDataListener l : onDataListeners) {
                            l.onPowerOff(data[0]);
                        }
                    }
                }
                break;
        }
    }

    /**
     * 判断校验和
     *
     * @param buffer
     * @return
     */
    public boolean checkSum(byte[] buffer) {
        int sum = 0;
        int dataLength = 0xff & (int) buffer[4]; // Data length
        int checkSumIndex = dataLength + 5;      // Checksum index
        for (int i = 5; i < checkSumIndex; i++) {
            sum += ((int) buffer[i] & 0xff);
        }
        sum &= 0xff;
        sum ^= 0xff;// According to the data definition
        if (sum != (0xff & buffer[checkSumIndex])) {
            Log.e(TAG, "sum=" + sum + "-----" + "checkSumIndex:" + (0xff & buffer[checkSumIndex]));
        }
        return sum == (0xff & buffer[checkSumIndex]);
    }

    public static byte intToByte(int x) {
        return (byte) x;
    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    /**
     * 两字节的byte转成int
     *
     * @param data
     * @return
     */
    public static int[] twoBytestoInt(byte[] data) {
        String s = HexStringUtils.bytesToHexString(data);
        int[] da = new int[s.length() / 4];
        try {
            for (int i = 0; i < s.length() / 4; i++) {
                String substring = s.substring(4 * i, 4 * (i + 1));
                String m = "";
                for (int j = substring.length(); j > 0; j -= 2) {
                    m = m + substring.substring(j - 2, j);
                }
                int d = Integer.parseInt(m, 16);
                da[i] = d;
            }
        } catch (Exception e) {
            Log.e(TAG, "Data parse error! " + e);
        }
        return da;
    }

    private List<OnDataListener> onDataListeners = new CopyOnWriteArrayList<>();

    public void addOnDataListener(OnDataListener onDataListener) {
        if (null != onDataListener) {
            if (!this.onDataListeners.contains(onDataListener)) {
                this.onDataListeners.add(onDataListener);
            }
        }
    }

    public void removeOnDataListener(OnDataListener onDataListener) {
        if (null != onDataListener) {
            this.onDataListeners.remove(onDataListener);
        }
    }

}
