package org.zcj.rpc.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zcj.rpc.common.model.Head;
import org.zcj.rpc.common.model.Protocol;
import org.zcj.rpc.serialization.Serializer;
import org.zcj.rpc.serialization.SerializerFactory;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 17 13
 * Description:
 */
public class RpcClientEncoder extends MessageToByteEncoder<Protocol> {

    private static Logger log = LoggerFactory.getLogger(RpcClientEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Protocol protocol, ByteBuf out) throws Exception {
        log.debug("send request to server : " + protocol);
        if (protocol == null || protocol.getHead() == null) {
            throw new Exception("protocol can not be null.");
        }
        Head head = protocol.getHead();
        Object body = protocol.getObject();
        Serializer serializer = SerializerFactory.getSerializer(head.getSerializer());
        byte[] bodyBytes = serializer.serialize(body);

        out.writeByte(head.getSerializer());                    // 1个字节
        out.writeByte(head.getType());                          // 1个字节
        out.writeBytes(head.getRequestId().getBytes());         // 32个字节
        int bodyBytesLen = bodyBytes.length;
        out.writeInt(bodyBytesLen);                                // 4个字节

        out.writeBytes(bodyBytes);
    }
}
