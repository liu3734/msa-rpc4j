package com.msa.rpc.client.handler;

import com.msa.rpc.common.bean.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.ConcurrentMap;

/**
 * The type Rpc client handler.
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse>{

    /**
     * The Response map.
     */
    private ConcurrentMap<String, RpcResponse> responseMap;

    /**
     * Instantiates a new Rpc client handler.
     *
     * @param responseMap the response map
     */
    public RpcClientHandler(ConcurrentMap<String, RpcResponse> responseMap) {
        this.responseMap = responseMap;
    }

    /**
     * Channel read 0.
     *
     * @param ctx      the ctx
     * @param response the response
     * @throws Exception the exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        // 建立请求id和应答结果之间的映射关系
        responseMap.put(response.getRequestId(), response);
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
