# UMindMirror Android SDK
English | [中文](./README_CN.md)

## Overview
This project provides an SDK and usage examples (Android) for the UMindMirror EEG device.

## Change Log
### **1.0.0**
1. The SDK allows you to access raw EEG data for research purposes related to EEG applications such as sleep monitoring and EEG control.
 
2. The SDK provides access to posture and movement data for research purposes related to posture and movement applications such as balance assessment.

3. The SDK allows you to connect to the device and determine the success or failure of the device connection.

4. The SDK provides access to information such as version number, SN number, and battery level.
   
We will be adding more features in the future, so stay tuned!

## API Manual
The usage process is: Initialize -> Search Device -> Configure Notch Filter -> Connect Device -> Receive Data -> Disconnect Device

### 1. Initialize
```java
AndroidBle.getInstance().initBleClient(getApplicationContext());
```

### 2. Search Device

Add search device listener
```java
Scanner702 scanner = new Scanner702();
Scanner702.Listener listener = new Scanner702.Listener() {
    @Override
    public void onDeviceListUpdated(ArrayList<SleepDevice> list) {
        // list: list of searched devices, updated every 2 seconds
    }

    @Override
    public void onScanning(boolean scan) {
        // scan: search status, true (start search), false (end search)
    }
};
scanner.addListener(listener);
```

Start the search and end the search automatically after 15 seconds
```java
scanner.startScan();
```

Stop search manually
```java
scanner.stopScan();
```

Remove search device listener
```java
scanner.removeListener(listener);
```

### 3. Connect Device

Configure the notch filter frequency of the device

**When there are high-power electrical appliances working near the device, it will affect the data collected by this device**

**It is recommended to configure the notch filter switch corresponding to the local power frequency before collecting data**

```java
// Configured to 60hz before connecting
BleConfig.notchHz = BleConfig.NOTCH_60;
// Switch to 60hz after connection
OrderUtils.close50Hz();
OrderUtils.open60Hz();
```

Add connected device listener
```java
private OnConnectListener onConnectListener = new OnConnectListener() {
    @Override
    public void onConnectStatus(final int status) {
        // status: device connection status
        // BleConfig.BLE_CONNECT (device connected successfully)
        // BleConfig.BLE_DISCONNECT (device disconnected)
    }

    @Override
    public void onRssi(int rssi) {
        // rssi: Device connection signal strength, need to call readRssi() first
    }
};
AndroidBle.getInstance().addOnConnectListener(onConnectListener);
```

Connect to the searched device
```java
AndroidBle.getInstance().connect(sleepDevice.getAddress());
```

Read the rssi of the current device connection, and return data only once per call
```java
AndroidBle.getInstance().readRssi();
```

Disconnect from current device
```java
AndroidBle.getInstance().disconnect();
```

Remove connected device listener
```java
AndroidBle.getInstance().removeOnConnectListener(onConnectListener);
```

### 4. Receive Data

Add data receive listener
```java
private OnDataListener OnDataListener = new OnDataListener() {
    @Override
    public void onHardwareVersion(String hwVersion) {
        // hwVersion: Device hardware version
    }

    @Override
    public void onDeviceVersion(String swVersion) {
        // swVersion: Device software version
    }

    @Override
    public void onDeviceSN(String sn) {
        // sn: Device SN
    }

    @Override
    public void onEegData(int[] eegData) {
        // eegData: EEG data, sampling rate 256hz
    }

    @Override
    public void onSignalQuality(int quality) {
        // quality: EEG signal quality
        // 0 or 20 (good signal), 200 (bad signal), other (signal detection)   
    }

    @Override
    public void onBattery(BatteryStatus batteryStatus，float battery，float voltage) {
        // batteryStatus: charging status
        // USING (not charging), CHARGING_NOT_FULL (charging), CHARGING_FULL (full charge)
        // battery: battery percentage
        // voltage: battery voltage
    }

    @Override
    public void onBodyMove(String bodyPosition，int position，int move） {
        // bodyPosition: body position
        // position: body position code, 0 (unknown), 1 (prone), 2 (left side)
        // 3 (supine), 4 (right side), 5 (upright), 6 (inverted), 7 (move)
        // move: body movement level, 0 (low) ~ 10 (high)
    }
};
DataParseUtil.getInstance().addOnDataListener(OnDataListener);
```

Remove data receive listener
```java
DataParseUtil.getInstance().removeOnDataListener(OnDataListener);
```

## License
[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Copyright © 2023 EEGSmart