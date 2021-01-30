package org.zcj.rpc.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.zcj.rpc.common.model.Head;
import org.zcj.rpc.common.model.Protocol;
import org.zcj.rpc.common.model.RpcResponse;
import org.zcj.rpc.serialization.SerializerFactory;

import java.util.List;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 17 13
 * Description:
 */
public class RpcClientDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() <= 38) {
            return;
        }
        byte serializer = in.readByte();
        byte type = in.readByte();
        byte[] requestIdBytes = new byte[32];
        in.readBytes(requestIdBytes);
        String requestId = new String(requestIdBytes);
        int dataLength = in.readInt();
        in.markReaderIndex();
        if (in.readableBytes() < dataLength){
            in.resetReaderIndex();
        }else {
            byte[] dst = new byte[dataLength];
            in.readBytes(dst);
            Object object = SerializerFactory.getSerializer(serializer).deSerialize(dst, RpcResponse.class);
            Protocol protocol = new Protocol();
            Head head = new Head();
            head.setSerializer(serializer);
            head.setType(type);
            head.setRequestId(requestId);
            head.setDataLength(dataLength);
            protocol.setHead(head);
            protocol.setObject(object);
            out.add(protocol);
        }
    }
}
