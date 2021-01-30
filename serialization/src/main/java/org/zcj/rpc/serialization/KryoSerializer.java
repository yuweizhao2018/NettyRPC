package org.zcj.rpc.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.BeanSerializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 10 45
 * Description:
 */
public class KryoSerializer implements Serializer {

    // kryo 是非线程安全类
    final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            Kryo kryo = new Kryo();
            return kryo;
        }
    };

    public KryoSerializer() {
    }


    private Kryo getKryo() {
        return kryoLocal.get();
    }



    @Override
    public byte[] serialize(Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Output output = new Output(bos);
        Kryo kryo = getKryo();
        kryo.register(obj.getClass(), new BeanSerializer(kryo, obj.getClass()));
        kryo.writeObjectOrNull(output, obj, obj.getClass());
        output.flush();
        output.close();
        byte[] bytes = bos.toByteArray();
        try {
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        ByteArrayInputStream bais = null;
        Input input = null;
        if (bytes == null)
            return null;
        try {
            bais = new ByteArrayInputStream(bytes);
            input = new Input(bais);
            Kryo kryo = getKryo();
            kryo.register(clazz, new BeanSerializer(kryo, clazz));
            return (T) kryo.readObjectOrNull(input, clazz);
        } finally {
            if (bais != null) {
                try {
                    bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (input != null) {
                input.close();
            }

        }

    }

}
