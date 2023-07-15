package com.eegsmart.blesdk.model;

/**
 * Created by Liusong on 2017/7/19.
 */

public enum SwitchType {
    /**
     * 开
     */
    SWITCH_ON(0x00),

    /**
     * 关
     */
    SWITCH_OFF(0x01);

    private int value;

    SwitchType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
