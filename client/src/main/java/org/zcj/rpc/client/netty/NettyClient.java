package org.zcj.rpc.client.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;
import org.zcj.rpc.client.codec.RpcClientDecoder;
import org.zcj.rpc.client.codec.RpcClientEncoder;
import org.zcj.rpc.client.core.RpcRequestPool;
import org.zcj.rpc.common.model.Protocol;
import org.zcj.rpc.common.model.RpcRequest;

import java.util.Set;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 11 40
 * Description:
 */
@Component
public class NettyClient {

    public static void send(Protocol protocol) throws Exception {
        RpcRequest request = (RpcRequest)protocol.getObject();
        String interfaceName = request.getClassName();
        String version = request.getVersion();
        Set<String> set = ChannelPool.serviceIps.get(interfaceName + version);

        Channel channel = ChannelPool.channels.get(set.toArray()[0]);
        channel.writeAndFlush(protocol);
        RpcRequestPool.getInstance().addRequest(protocol.getHead().getRequestId(), channel.eventLoop());
    }

    public static Channel createChannel(String ip, int port) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(nioEventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY,true)
                .remoteAddress(ip, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        // todo
                        pipeline.addLast(new RpcClientEncoder());
                        pipeline.addLast(new RpcClientDecoder());
                        pipeline.addLast(new NettyClientHandler());
                    }
                });
        try {
            Channel channel = bootstrap.connect().sync().channel();
            return channel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
