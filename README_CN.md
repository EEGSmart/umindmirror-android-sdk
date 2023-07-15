# UMindMirror Android SDK
[English](./README.md) | 中文

## 概述
本项目提供了适用于UMindMirror脑电设备的SDK和使用示例（Android）。

## 版本更新
### **1.0.0**
1. 可通过SDK获取脑电的原始数据，进行关于脑电相关应用的研究。如睡眠监测，脑电控制等；

2. 可通过SDK获得体位和体动的数据，进行关于体动和体位相关应用的研究。如平衡力等；

3. 可通过SDK连接设备以及判断设备连接的成功或失败的状态；

4. 可通过SDK获取版本号、SN号、电量等相关信息

之后我们将会开放更多的功能，敬请期待！

## API手册
使用流程为: 初始化 -> 搜索设备 -> 配置陷波器 -> 连接设备 -> 接收数据 -> 断连设备

### 1. 初始化
```java
AndroidBle.getInstance().initBleClient(getApplicationContext());
```

### 2. 搜索设备
添加搜索设备监听器
```java
Scanner702 scanner = new Scanner702();
Scanner702.Listener listener = new Scanner702.Listener() {
    @Override
    public void onDeviceListUpdated(ArrayList<SleepDevice> list) {
       // list: 搜索到的设备列表，每2秒更新
    }

    @Override
    public void onScanning(boolean scan) {
        // scan: 搜索状态，true（开始搜索），false（结束搜索）
    }
};
scanner.addListener(listener);
```

开始搜索，15秒后自动结束搜索
```java
scanner.startScan();
```

手动结束搜索
```java
scanner.stopScan();
```

移除搜索设备监听器
```java
scanner.removeListener(listener);
```

### 3. 连接设备

配置设备的陷波器频率  

**当设备附近有大功率电器工作时会影响此设备采集的数据**  

**建议采集数据前先配置当地工频对应的陷波器开关**

```java
// 连接前配置为60hz
BleConfig.notchHz = BleConfig.NOTCH_60;
// 连接后切换为60hz
OrderUtils.close50Hz();
OrderUtils.open60Hz();
```

添加连接设备监听器
```java
private OnConnectListener onConnectListener = new OnConnectListener() {
    @Override
    public void onConnectStatus(final int status) {
        // status: 设备连接状态 
        // BleConfig.BLE_CONNECT（设备连接成功）
        // BleConfig.BLE_DISCONNECT（设备断开连接）
    }

    @Override
    public void onRssi(int rssi) {
        // rssi: 设备连接信号强度，需先调用readRssi()
    }
};
AndroidBle.getInstance().addOnConnectListener(onConnectListener);
```

连接搜索到的设备
```java
AndroidBle.getInstance().connect(sleepDevice.getAddress());
```

读取当前设备连接的rssi，每次调用只返回一次数据
```java
AndroidBle.getInstance().readRssi();
```

断开与当前设备的连接
```java
AndroidBle.getInstance().disconnect();
```

移除连接设备监听器
```java
AndroidBle.getInstance().removeOnConnectListener(onConnectListener);
```

### 4. 接收数据

添加数据接收监听器
```java
private OnDataListener OnDataListener = new OnDataListener() {
    @Override
    public void onHardwareVersion(String hwVersion) {
        // hwVersion: 设备硬件版本
    }

    @Override
    public void onDeviceVersion(String swVersion) {
        // swVersion: 设备软件版本
    }

    @Override
    public void onDeviceSN(String sn) {
        // sn: 设备SN
    }

    @Override
    public void onEegData(int[] eegData) {
        // eegData: 脑电数据，采样率256hz
    }

    @Override
    public void onSignalQuality(int quality) {
        // quality: 脑电信号质量
        // 0或20（信号良好），200（信号不良），其他（信号检测中）
    }

    @Override
    public void onBattery(BatteryStatus batteryStatus，float battery，float voltage) {
        // batteryStatus: 充电状态
        // USING（未充电），CHARGING_NOT_FULL（充电中），CHARGING_FULL（充满电）
        // battery: 电量百分比
        // voltage: 电池电压
    }

    @Override
    public void onBodyMove(String bodyPosition，int position，int move） {
        // bodyPosition: 体位
        // position: 体位代码，0（未知），1（俯卧），2（左侧卧） 
        // 3（仰卧），4（右侧卧），5（直立），6（倒立），7（移动）
        // move: 体动等级，0（低）~ 10（高）
    }
};
DataParseUtil.getInstance().addOnDataListener(OnDataListener);
```

移除数据接收监听器
```java
DataParseUtil.getInstance().removeOnDataListener(OnDataListener);
```

## 许可证
[Apache-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Copyright © 2023 EEGSmart