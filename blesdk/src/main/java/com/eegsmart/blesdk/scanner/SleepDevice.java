package com.eegsmart.blesdk.scanner;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

import com.eegsmart.blesdk.model.BatteryStatus;

/**
 * 睡眠设备类  基于702
 */
public final class SleepDevice implements Parcelable, Comparable {

    private String name = "";
    private String address = "";
    private String sn = "--";
    private String versionHard = "1.0.0";
    private String versionSoft = "--";
    private String model = MODEL_HST_CE;
    private int rssi = 0;
    private float voltage = 0f;
    private float battery = -1;

    private BatteryStatus chargeState = BatteryStatus.USING; //0未充电, 1充电中, 2充满电

    public static final int NOISE_WEAR = 0; // 佩戴
    public static final int NOISE_WEAR_ALGO = 20; // 佩戴
    public static final int NOISE_FALL = 200; // 脱落
    private int wear = NOISE_FALL;

    public int getWear() {
        return wear;
    }

    public void setWear(int wear) {
        this.wear = wear;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getBattery() {
        return battery;
    }

    public void setBattery(float battery) {
        this.battery = battery;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public BatteryStatus getChargeState() {
        return chargeState;
    }

    public void setChargeState(BatteryStatus chargeState) {
        this.chargeState = chargeState;
    }

    public static final Creator<SleepDevice> CREATOR = new Creator<SleepDevice>() {
        @Override
        public SleepDevice createFromParcel(Parcel in) {
            return new SleepDevice(in);
        }

        @Override
        public SleepDevice[] newArray(int size) {
            return new SleepDevice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeString(name);
        dest.writeInt(rssi);
    }

    @Override
    public boolean equals(Object obj) {
        if (null == obj || !(obj instanceof SleepDevice)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        SleepDevice likeThis = (SleepDevice) obj;
        return ((likeThis.getName().equals(this.getName())) &&
                (likeThis.getAddress().equals(this.getAddress())));
    }

    @Override
    public String toString() {
        return name + " " + address + " " + rssi;
    }

    /**
     * larger is previous
     */
    @Override
    public int compareTo(Object o) {
        SleepDevice esObj = (SleepDevice) o;
        int rssiEs = esObj.getRssi();
        if (this.rssi > rssiEs) {
            return -1;
        } else if (this.rssi < rssiEs) {
            return 1;
        }
        return 0;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
        updateModel();
    }

    public String getVersionHard() {
        return versionHard;
    }

    public void setVersionHard(String versionHard) {
        this.versionHard = versionHard;
    }

    public String getVersionSoft() {
        return versionSoft;
    }

    public void setVersionSoft(String versionSoft) {
        this.versionSoft = versionSoft;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public final static String MODEL_702_S1 = "S0";
    public final static String MODEL_902_S1 = "S1";
    public final static String MODEL_902_E1 = "E1";

    public final static String MODEL_HST_C1 = "C1";
    public final static String MODEL_HST_H1 = "H1";
    public final static String MODEL_HST_CE = "CE";
    public final static String MODEL_HST_UM = "UM";

    private void updateModel(){
        String model = MODEL_902_S1;
        if(sn.length() >= 2){
            model = sn.substring(0, 2);
            if(model.equals("S1")){
                if(versionHard.compareTo("1") < 0){
                    model = MODEL_702_S1;
                }
            }
        }

        setModel(model);
    }

    public boolean isHst(){
        return model.equals(MODEL_HST_C1) || model.equals(MODEL_HST_H1) ||
                model.equals(MODEL_HST_CE) || model.equals(MODEL_HST_UM);
    }

    public SleepDevice(){

    }

    public SleepDevice(String sn){
        setSn(sn);
    }

    public SleepDevice(BluetoothDevice device) {
        this.address = device.getAddress();
        this.name = device.getName();
    }

    protected SleepDevice(Parcel in) {
        address = in.readString();
        name = in.readString();
        rssi = in.readInt();
    }

    /**
     * 是否充电中
     * @return 是否
     */
    public boolean isCharge(){
        return chargeState == BatteryStatus.CHARGING_FULL || chargeState == BatteryStatus.CHARGING_NOT_FULL;
    }

}
