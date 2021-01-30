package org.zcj.rpc.common.model;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 10 45
 * Description:
 */
public class Head {

    // 序列化方式
    private byte serializer;

    // 是否是心跳检测0否 1 是(默认否)
    private byte type = (byte) 0;

    // 32个字节的uuid
    private String requestId;

    // 数据长度
    private int dataLength;

    public byte getSerializer() {
        return serializer;
    }

    public void setSerializer(byte serializer) {
        this.serializer = serializer;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getDataLength() {
        return dataLength;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }
}
