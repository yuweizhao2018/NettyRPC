package org.zcj.rpc.serialization;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/15 17 42
 * Description:
 */
public enum SerializerEnum {

    JSON((byte)0),
    KRYO((byte)1),
    protoBuff((byte)2);

    private final byte value;

    private SerializerEnum(byte value) {
        this.value = value;
    }

    public SerializerEnum parse(byte value) {
        for (SerializerEnum rpcFormatter : SerializerEnum.values()) {
            if (rpcFormatter.value == value) {
                return rpcFormatter;
            }
        }
        return JSON;
    }

    public byte value() {
        return this.value;
    }

}
