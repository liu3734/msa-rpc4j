package com.msa.rpc.client.handler;

import com.msa.rpc.common.bean.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentMap;

/**
 * The type Rpc client handler.
 *
 * @ClassName: RpcClientHandler
 * @Description: 客户端处理类
 * @Author: sxp
 * @Date: 15 :28 2018/4/28
 * @Version: 1.0.0
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse>{

    /**
     * The Response map.
     */
    private ConcurrentMap<String, RpcResponse> responseMap;

    /**
     * see {@link #SimpleChannelInboundHandler(boolean)} with {@code true} as boolean parameter.
     *
     * @param responseMap the response map
     */
    public RpcClientHandler(ConcurrentMap<String, RpcResponse> responseMap) {
        this.responseMap = responseMap;
    }

    /**
     * <strong>Please keep in mind that this method will be renamed to
     * {@code messageReceived(ChannelHandlerContext, I)} in 5.0.</strong>
     * <p>
     * Is called for each message of type {@link I}.
     *
     * @param ctx      the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}            belongs to
     * @param response the response
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        // 建立请求id和应答结果之间的映射关系
        responseMap.put(response.getRequestId(), response);
    }

    /**
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
