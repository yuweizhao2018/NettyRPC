package org.zcj.rpc.serialization;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 10 45
 * Description:
 */
public class SerializerFactory {

    public static Serializer getSerializer(byte value) {

        if (SerializerEnum.JSON.value() == value) {
            return new JsonSerializer();
        }
        if (SerializerEnum.KRYO.value() == value) {
            return new KryoSerializer();
        }
        return new JsonSerializer();
    }
}
