package com.eegsmart.blesdk.model;

/**
 * Created by Liusong on 2017/7/19.
 */

public enum ClassType {

    /**
     * 控制开关
     */
    CONTROL_SWITCH(0x22),

    /**
     * 控制返回数据
     */
    CONTROL_DATA(0x23);

    private int value;

    ClassType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static ClassType getType(int type){
        switch (type){
            case 0x23:
                return ClassType.CONTROL_DATA;
            default:
                return ClassType.CONTROL_SWITCH;
        }
    }
}
