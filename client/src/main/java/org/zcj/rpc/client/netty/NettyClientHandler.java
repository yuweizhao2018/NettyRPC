package org.zcj.rpc.client.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.zcj.rpc.client.core.RpcRequestPool;
import org.zcj.rpc.common.model.Protocol;
import org.zcj.rpc.common.model.RpcResponse;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 17 41
 * Description:
 */
public class NettyClientHandler extends SimpleChannelInboundHandler<Protocol> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("fasfa");
        System.out.println("NettyClientHandler......channelActive............");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("NettyClientHandler......channelInactive............");
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Protocol protocol) throws Exception {
        System.out.println("NettyClientHandler...channelRead0...");
        RpcRequestPool.getInstance().notifyRequest(protocol.getHead().getRequestId(), protocol);
    }
}
