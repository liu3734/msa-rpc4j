package com.msa.rpc.server.handler;

import com.msa.rpc.common.bean.RpcRequest;
import com.msa.rpc.common.bean.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * The type Rpc server req handler.
 */
@Slf4j
public class RpcServerReqHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * 用于存放服务名称和服务实例之间的映射
     */
    private final Map<String, Object> handlerMap;

    /**
     * Instantiates a new Rpc server req handler.
     *
     * @param handlerMap the handler map
     */
    public RpcServerReqHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * Channel read 0.
     *
     * @param ctx     the ctx
     * @param request the request
     * @throws Exception the exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        RpcResponse response = RpcResponse.builder().requestId(request.getRequestId())
                .build();
        try {
            Object result = handleRequest(request);
            response.setResult(result);
        } catch (Exception e) {
            response.setException(e);
            log.error(">>>>>>>>===handle rpc request failure", e);
        }
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 处理RPC请求
     * Handle request object.
     *
     * @param request the request
     * @return the object
     * @throws Exception the exception
     */
    private Object handleRequest(RpcRequest request) throws Exception {
        String serviceName = request.getInterfaceName();
        Object serviceBean = handlerMap.get(serviceName);
        if (Objects.isNull(serviceBean)) {
            throw new RuntimeException(String.format("can not find service bean by key %s", serviceName));
        }

        // 反射调用内部方法
        String methodName = request.getMethodName();
        Class<?>[] parameterType = request.getParameterType();
        Object[] parameters = request.getParameters();
        Method method = ReflectionUtils.findMethod(serviceBean.getClass(), methodName, parameterType);
        ReflectionUtils.makeAccessible(method);
        return ReflectionUtils.invokeMethod(method, serviceBean, parameters);
    }

    /**
     * Exception caught.
     *
     * @param ctx   the ctx
     * @param cause the cause
     * @throws Exception the exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
