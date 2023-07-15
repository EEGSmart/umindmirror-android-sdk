package com.eegsmart.blesdk.check;

/**
 * 设备状态指令
 */
public final class OrderState {

    public enum Type {
        GET_DEV_SW_MSG,        //固件软件版本
        GET_SN_NUMBER,         //sn序列号
        GET_HD_VERSION,         //硬件版本
        GET_SYS_TIME,          //时间同步
        GET_SYS_OPEN_DATE,     //数据开关
        GET_OVER,              //结束
    }

    private Type type = Type.GET_HD_VERSION;

    public OrderState(Type type) {
        this.type = type;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
