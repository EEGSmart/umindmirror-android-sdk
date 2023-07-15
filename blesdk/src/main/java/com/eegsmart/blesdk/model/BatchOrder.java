package com.eegsmart.blesdk.model;

public class BatchOrder {

    public boolean ERR_MSG = false;
    public boolean OTHER = false;
    public boolean ECG_ALGO = false;
    public boolean PRESS_FLOW_DATA = false;
    public boolean THER_FLOW_DATA = false;
    public boolean NOTCH_60HZ_FILTER = false;
    public boolean UPDATE_DEV_NAME = false;
    public boolean UPDATE_REPORT = false;
    public boolean RECORD_REPORT = false;
    public boolean INQUIRE_DEVICE_SN_MSG = false;
    public boolean INQUIRE_DEVICE_SW_MSG = false;
    public boolean INQUIRE_DEVICE_HW_MSG = false;
    public boolean INQUIRE_DEVICE_STATE = false;
    public boolean UPDATA_FIRMWARE = false;
    public boolean SYS_TIME = false;
    public boolean OFFLINE_MODE = false;
    public boolean FIR_FILTER = false;
    public boolean BATTERY_VAL_DATA = false;
    public boolean BODY_TEMP_DATA = false;
    public boolean GYRO_TEMP_DATA = false;
    public boolean MIC_ALGO = false;
    public boolean MIC_DATA = false;
    public boolean HR_SPO2_ALGO = false;
    public boolean HR_SPO2_DATA = false;
    public boolean GYRO_ALGO = false;
    public boolean GYRO_DATA = false;
    public boolean EEG_ALGO = false;
    public boolean EEG_DATA = false;
    public boolean MEDITATION_ESENSE = false;
    public boolean ATTENTION_ESENSE = false;
    public boolean POOR_SIGNAL_QUALITY = false;
    public boolean UNKNOWN = false;

    public String getValue1() {
        return (ERR_MSG ? "0" : "1")
                + (OTHER ? "0" : "1")
                + (ECG_ALGO ? "0" : "1")
                + (PRESS_FLOW_DATA ? "0" : "1")
                + (THER_FLOW_DATA ? "0" : "1")
                + (NOTCH_60HZ_FILTER ? "0" : "1")
                + (UPDATE_DEV_NAME ? "0" : "1")
                + (UPDATE_REPORT ? "0" : "1")
                + (RECORD_REPORT ? "0" : "1")
                + (INQUIRE_DEVICE_SN_MSG ? "0" : "1")
                + (INQUIRE_DEVICE_SW_MSG ? "0" : "1")
                + (INQUIRE_DEVICE_HW_MSG ? "0" : "1")
                + (INQUIRE_DEVICE_STATE ? "0" : "1")
                + (UPDATA_FIRMWARE ? "0" : "1")
                + (SYS_TIME ? "0" : "1")
                + (OFFLINE_MODE ? "0" : "1")
                + (FIR_FILTER ? "0" : "1")
                + (BATTERY_VAL_DATA ? "0" : "1")
                + (BODY_TEMP_DATA ? "0" : "1")
                + (GYRO_TEMP_DATA ? "0" : "1")
                + (MIC_ALGO ? "0" : "1")
                + (MIC_DATA ? "0" : "1")
                + (HR_SPO2_ALGO ? "0" : "1")
                + (HR_SPO2_DATA ? "0" : "1")
                + (GYRO_ALGO ? "0" : "1")
                + (GYRO_DATA ? "0" : "1")
                + (EEG_ALGO ? "0" : "1")
                + (EEG_DATA ? "0" : "1")
                + (MEDITATION_ESENSE ? "0" : "1")
                + (ATTENTION_ESENSE ? "0" : "1")
                + (POOR_SIGNAL_QUALITY ? "0" : "1")
                + (UNKNOWN ? "0" : "1");
    }
}
