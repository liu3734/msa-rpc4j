package com.msa.rpc.client;

import com.google.common.collect.Maps;
import com.msa.api.regcovery.discovery.ServiceDiscovery;
import com.msa.rpc.client.handler.RpcClientHandler;
import com.msa.rpc.common.bean.RpcRequest;
import com.msa.rpc.common.bean.RpcResponse;
import com.msa.rpc.common.codec.RpcDecoder;
import com.msa.rpc.common.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentMap;

/**
 * The type Rpc client.
 */
@Slf4j
@Data
@NoArgsConstructor
public class RpcClient {
    /**
     * The Service discovery.
     */
    private ServiceDiscovery serviceDiscovery;

    /**
     * requestId与RpcResponse映射
     * The constant responseMap.
     */
    private static final ConcurrentMap<String, RpcResponse> responseMap = Maps.newConcurrentMap();

    /**
     * Instantiates a new Rpc client.
     *
     * @param serviceDiscovery the service discovery
     */
    public RpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * Load balance string.
     *
     * @param serviceName the service name
     * @return the string
     */
    public String loadBalance(String serviceName) {
        return serviceDiscovery.discover(serviceName);
    }

    /**
     * Send request rpc response.
     *
     * @param host    the host
     * @param port    the port
     * @param request the request
     * @return the rpc response
     */
    public RpcResponse sendRequest(String host, int port, RpcRequest request) {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {

                /**
                 * This method will be called once the {@link Channel} was registered. After the method returns this instance
                 * will be removed from the {@link ChannelPipeline} of the {@link Channel}.
                 *
                 * @param ch the {@link Channel} which was registered.
                 * @throws Exception is thrown if an error occurs. In that case it will be handled by
                 *                   {@link #exceptionCaught(ChannelHandlerContext, Throwable)} which will by default close
                 *                   the {@link Channel}.
                 */
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new RpcEncoder(RpcRequest.class));
                    pipeline.addLast(new RpcDecoder(RpcResponse.class));
                    pipeline.addLast(new RpcClientHandler(responseMap));
                }
            });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            channel.writeAndFlush(request).sync();
            channel.closeFuture().sync();
            return responseMap.get(request.getRequestId());
        } catch (Exception e) {
            log.error(">>>>>>>>===send request exception", e);
            return null;
        } finally {
            //调用返回结果后，根据请求id移除应答结果，防止内存泄漏
            responseMap.remove(request.getRequestId());
            group.shutdownGracefully();
        }
    }
}
