package com.eegsmart.blesdk.model;

/**
 * 设备电池状态
 * Create on 2018-11-30
 */
public enum BatteryStatus {

    USING /* 放电中 */,
    CHARGING_NOT_FULL /* 充电中 但没充满 */,
    CHARGING_FULL /* 已充满 但没拔电 */

}
