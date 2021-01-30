package org.zcj.rpc.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zcj.rpc.common.config.NettyProperties;
import org.zcj.rpc.serialization.SerializerFactory;
import org.zcj.rpc.server.codec.RpcServerDecoder;
import org.zcj.rpc.server.codec.RpcServerEncoder;

import javax.annotation.PreDestroy;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/13 16 34
 * Description:
 */
@Component
public class NettyServer {

    @Autowired
    private NettyProperties nettyProperties;

    private Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private ServerBootstrap serverBootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workGroup;

    @Autowired
    private NettServerHandler nettServerHandler;

    private NettyServer() {
        serverBootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup();

        serverBootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RpcServerDecoder());
                        ch.pipeline().addLast(new RpcServerEncoder());
                        ch.pipeline().addLast(nettServerHandler);
                    }
                });
    }

    public void openServer() {
        try {
            ChannelFuture future = serverBootstrap.bind(nettyProperties.getServerPort()).sync();
            if (future.isSuccess()) {
                logger.info("netty 服务器启动....");
            }
        } catch (InterruptedException e) {
            logger.info("netty 服务器启动失败....");
        }
    }

    @PreDestroy
    public void shutDown(){
        workGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }
}
