目录结构：
baseble: 蓝牙的基类
    IBle.java  -->  一些蓝牙的抽象接口
    AndroidBle.java  -->  IBle的子类，封装了蓝牙的基本操作
    BleConfig.java  -->  一些蓝牙的服务特征值和常量
bean: 数据格式
    BufferStructure.java  -->  一个数据包的结构
    DeviceStatus.java --> 获取蓝牙状态信息的数据结构
    ESGyroEntity.java --> 陀螺仪数据的结构
    Spo2Entity.java --> 血氧数据的结构
callback: 回调函数
    HScanCallback.java  --> 代替过时版本的蓝牙搜索回调
listener: 接口监听
    OnCharacteristicListener.java  --> 监听收到蓝牙的原始数据(注释掉了，因为会用其他接口传递解析完的数据)
    OnConnectListener.java --> 蓝牙连接状态的监听
    OnDataListener.java --> 解析完毕数据的监听(包含:信号质量，集中程度，放松程度，脑电数据，干扰值，陀螺仪算法数据，心率血氧算法数据，陀螺仪温度数据，人体温度数据，电池电压数据)，后续再补充
    OnDataOpenListener.java --> 用于监听哪些数据打开了，哪些数据关闭了
    OnDeviceStatusListener.java --> 获取设备状态信息的监听
    OnScanBleListenr.java --> 搜索蓝牙设备结果的监听
    OnServiceListener.java --> 发现服务和特征值的监听
model: 协议解析
    ClassType.java --> 区分是测试还是控制，是发出的数据还是收到的数据
    ControlType.java --> 基本控制类型，解析也依据这个
    SendControlOrder.java --> 拼接控制指令
    SendOrder.java --> SendControlOrder的父类
    SwitchType.java --> 开关控制
    TestType.java --> 测试类(开发中)
util: 工具包
    DataParseUtil.java --> 数据解析工具
    orderUtils.java --> 所有的控制指令


