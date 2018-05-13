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
 *
 * @ClassName: RpcServerReqHandler
 * @Description: 服务端处理请求类
 * @Author: sxp
 * @Date: 14 :42 2018/4/28
 * @Version: 1.0.0
 */
@Slf4j
public class RpcServerReqHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * 用于存放服务名称和服务实例之间的映射
     */
    private final Map<String, Object> handlerMap;

    /**
     * see {@link #SimpleChannelInboundHandler(boolean)} with {@code true} as boolean parameter.
     *
     * @param handlerMap the handler map
     */
    public RpcServerReqHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }

    /**
     * 读取通道中的数据
     * <strong>Please keep in mind that this method will be renamed to
     * {@code messageReceived(ChannelHandlerContext, I)} in 5.0.</strong>
     * <p>
     * Is called for each message of type {@link I}.
     *
     * @param ctx     the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}            belongs to
     * @param request the request
     * @throws Exception is thrown if an error occurred
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
        // 写入RPC应答结果，立即关闭和客户端的连接，防止连接数达到上限，线程堵塞
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
     * 用于捕获RPC通信中的异常
     * Calls {@link ChannelHandlerContext#fireExceptionCaught(Throwable)} to forward
     * to the next {@link ChannelHandler} in the {@link ChannelPipeline}.
     * <p>
     * Sub-classes may override this method to change behavior.
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
