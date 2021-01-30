package org.zcj.rpc.serialization;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 10 45
 * Description:
 */
public interface Serializer {
    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    public <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param bytes
     * @param cls
     * @param <T>
     * @return
     */
    public <T> T deSerialize(byte[] bytes, Class<T> cls);
}
