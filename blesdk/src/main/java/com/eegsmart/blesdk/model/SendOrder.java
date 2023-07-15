package com.eegsmart.blesdk.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Liusong on 2017/7/19.
 */

public class SendOrder {

    public final int ORDER_HEADER = 0xaa;
    private ControlType controlType;
    private SwitchType switchType;
    private List<Integer> dataList;   //十进制

    public SendOrder(ControlType type,SwitchType switchType) {
        this.controlType = type;
        this.switchType = switchType;
        dataList = new ArrayList<>();
    }

    public ControlType getControlType() {
        return controlType;
    }

    public SwitchType getSwitchType() {
        return switchType;
    }

    public void add(Integer data){
        dataList.add(data);
    }

    public void add(int position, Integer data){
        dataList.add(position,data);
    }

    public void add(Integer... dataArray){
        if(dataArray != null){
            dataList.addAll(Arrays.asList(dataArray));
        }
    }

    public int size(){
        return dataList.size();
    }

    public List<Integer> getDataList() {
        return dataList;
    }

    public static String formatToHexStr(int value) {
        String s = Integer.toHexString(value);
        if(s.length() == 1){
            s = "0" + s;
        }
        return s;
    }


    public SendOrder(ControlType type) {
        this.controlType = type;
        dataList = new ArrayList<>();
    }

    public void add(int position, byte[] data){
        List<Integer> list = new ArrayList<>();
        for (byte datum : data) {
            list.add((int) datum);
        }
        dataList.addAll(list);
    }
}
