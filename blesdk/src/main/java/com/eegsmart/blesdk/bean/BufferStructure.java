package com.eegsmart.blesdk.bean;

/**
 * 数据包的结构
 * Created by Liusong on 2017/7/21.
 */

public class BufferStructure {
    private int classType;
    private int head;
    private int lenth;
    private byte[] data = new byte[0];
    private byte[] raw = new byte[0];

    public BufferStructure(int classType, int head, int lenth, byte[] data) {
        this.classType = classType;
        this.head = head;
        this.lenth = lenth;
        this.data = data;
    }

    public int getClassType() {
        return classType;
    }

    public void setClassType(int classType) {
        this.classType = classType;
    }

    public int getHead() {
        return head;
    }

    public void setHead(int head) {
        this.head = head;
    }

    public int getLenth() {
        return lenth;
    }

    public void setLenth(int lenth) {
        this.lenth = lenth;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getRaw() {
        return raw;
    }

    public void setRaw(byte[] raw) {
        this.raw = raw;
    }
}
