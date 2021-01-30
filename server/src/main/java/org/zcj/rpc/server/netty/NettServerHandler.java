package org.zcj.rpc.server.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.zcj.rpc.common.model.Protocol;
import org.zcj.rpc.common.model.RpcRequest;
import org.zcj.rpc.common.model.RpcResponse;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * Author: yuzhaozhao
 * Date: 2020/6/14 17 35
 * Description:
 */
@Component
@ChannelHandler.Sharable
public class NettServerHandler extends SimpleChannelInboundHandler<Protocol> {

    private Logger logger = LoggerFactory.getLogger(NettServerHandler.class);

    @Resource
    private ApplicationContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("NettServerHandler......channelActive..........");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("NettServerHandler......channelInactive..........");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Protocol protocol) throws Exception {
        logger.debug("NettServerHandler......channelRead0..........reqestId...." + protocol.getHead().getRequestId());
        Protocol resProtocol = new Protocol();
        RpcResponse rpcResponse = new RpcResponse();
        try {
            RpcRequest request = (RpcRequest) protocol.getObject();
            String className = request.getClassName();
            Object serviceImpl = context.getBean(Class.forName(className));
            Method targetMethod = serviceImpl.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            Object invoke = targetMethod.invoke(serviceImpl, request.getParameters());
            rpcResponse.setResult(invoke);
        } catch (Exception e) {
            e.printStackTrace();
            rpcResponse.setException(e);
        }
        resProtocol.setObject(rpcResponse);
        resProtocol.setHead(protocol.getHead());
        ctx.writeAndFlush(resProtocol);
    }
}
