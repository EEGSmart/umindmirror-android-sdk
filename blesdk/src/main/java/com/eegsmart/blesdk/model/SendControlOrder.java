package com.eegsmart.blesdk.model;

/**
 * 发送控制指令
 * Created by Liusong on 2017/7/19.
 */

public class SendControlOrder extends SendOrder {

    public SendControlOrder(ControlType type, SwitchType switchType) {
        super(type, switchType);
        add(0, 0x00);    //应答：请求是0，应答是1
        add(0, getSwitchType().getValue());  //数据：开/关
        add(0, size());    //数据的长度
        add(0, getControlType().getValue()); //控制类型:比如是脑电数据还是脑电算法
        add(0, ClassType.CONTROL_SWITCH.getValue());  //控制开关
        add(0, size());    //数据的长度
    }

    public SendControlOrder(ControlType type, int data) {
        super(type);

        add(0, 0x00);    //应答：请求是0，应答是1
        add(0, data);  //数据：开/关
        add(0, size());    //数据的长度
        add(0, getControlType().getValue()); //控制类型:比如是脑电数据还是脑电算法
        add(0, ClassType.CONTROL_SWITCH.getValue());  //控制开关
        add(0, size());    //数据的长度
    }

    public SendControlOrder(ControlType type, byte[] data) {
        super(type);

        add(0, data);    //应答：请求是0，应答是1
        add(0, size());    //数据的长度

        add(0, getControlType().getValue()); //控制类型:比如是脑电数据还是脑电算法
        add(0, ClassType.CONTROL_DATA.getValue());  //控制开关
        add(0, size());    //数据的长度
    }

    public String generateString() {
        StringBuilder builder = new StringBuilder();
        int checkSum = 0;
        if (size() > 0) {
            for (int i = 1; i < size(); i++) {
                checkSum = checkSum + getDataList().get(i);
            }
        }
        //拼接命令
        builder.append(formatToHexStr(ORDER_HEADER));
        builder.append(formatToHexStr(ORDER_HEADER));
        builder.append("00");
        builder.append("00");
        for (int i = 0; i < size(); i++) {
            builder.append(formatToHexStr(getDataList().get(i)));
        }
        checkSum = 0xff - (checkSum & 0xff);
        builder.append(formatToHexStr(checkSum));
        return builder.toString();
    }
}
