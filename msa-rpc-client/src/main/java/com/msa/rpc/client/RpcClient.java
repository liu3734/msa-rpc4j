package com.msa.rpc.client;

import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.reflect.Reflection;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

/**
 * The type Rpc client.
 *
 * @ClassName: RpcClient
 * @Description: 服务客户端
 * @Author: sxp
 * @Date: 15 :27 2018/4/28
 * @Version: 1.0.0
 */
@Slf4j
@Data
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
     * New instance t.
     *
     * @param <T>            the type parameter
     * @param interfaceClazz the interface clazz
     * @return the t
     */
    public <T> T newInstance(final Class<T> type, final Class interfaceClazz) {
        return Reflection.newProxy(type, new RpcInvocationHandler(serviceDiscovery, interfaceClazz));
    }

    /**
     * The type Rpc invocation handler.
     *
     * @param <T> the type parameter
     */
    public static class RpcInvocationHandler<T> implements InvocationHandler {
        /**
         * The Service discovery.
         */
        private ServiceDiscovery serviceDiscovery;
        /**
         * The Interface clazz.
         */
        private Class<T> interfaceClazz;

        /**
         * Instantiates a new Rpc invocation handler.
         *
         * @param serviceDiscovery the service discovery
         * @param interfaceClazz   the interface clazz
         */
        public RpcInvocationHandler(ServiceDiscovery serviceDiscovery, Class<T> interfaceClazz) {
            this.serviceDiscovery = serviceDiscovery;
            this.interfaceClazz = interfaceClazz;
        }

        /**
         * Processes a method invocation on a proxy instance and returns
         * the result.  This method will be invoked on an invocation handler
         * when a method is invoked on a proxy instance that it is
         * associated with.
         *
         * @param proxy  the proxy instance that the method was invoked on
         * @param method the {@code Method} instance corresponding to               the interface method invoked on the proxy instance.  The declaring               class of the {@code Method} object will be the interface that               the method was declared in, which may be a superinterface of the               proxy interface that the proxy class inherits the method through.
         * @param args   an array of objects containing the values of the               arguments passed in the method invocation on the proxy instance,               or {@code null} if interface method takes no arguments.               Arguments of primitive types are wrapped in instances of the               appropriate primitive wrapper class, such as               {@code java.lang.Integer} or {@code java.lang.Boolean}.
         * @return the value to return from the method invocation on the proxy instance.  If the declared return type of the interface method is a primitive type, then the value returned by this method must be an instance of the corresponding primitive wrapper class; otherwise, it must be a type assignable to the declared return type.  If the value returned by this method is {@code null} and the interface method's return type is primitive, then a {@code NullPointerException} will be thrown by the method invocation on the proxy instance.  If the value returned by this method is otherwise not compatible with the interface method's declared return type as described above, a {@code ClassCastException} will be thrown by the method invocation on the proxy instance.
         * @throws Throwable the exception to throw from the method                   invocation on the proxy instance.  The exception's type must be                   assignable either to any of the exception types declared in the                   {@code throws} clause of the interface method or to the                   unchecked exception types {@code java.lang.RuntimeException}                   or {@code java.lang.Error}.  If a checked exception is                   thrown by this method that is not assignable to any of the                   exception types declared in the {@code throws} clause of                   the interface method, then an                   {@link UndeclaredThrowableException} containing the                   exception that was thrown by this method will be thrown by the                   method invocation on the proxy instance.
         * @see UndeclaredThrowableException
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            RpcRequest request = RpcRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .interfaceName(method.getDeclaringClass().getName())
                    .methodName(method.getName())
                    .parameterType(method.getParameterTypes())
                    .parameters(args)
                    .build();
            String serviceName = interfaceClazz.getName();
            String hostAndPort = serviceDiscovery.discover(serviceName);
            log.debug(">>>>>>>>===discover service: {} ===> {}", serviceName, hostAndPort);
            if (StringUtils.isEmpty(hostAndPort)) {
                throw new RuntimeException(">>>>>>>>===server address is empty");
            }
            HostAndPort var0 = HostAndPort.fromString(hostAndPort);
            String host = var0.getHost();
            int port = var0.getPort();
            RpcResponse response = sendRequest(host, port, request);
            if (Objects.isNull(response)) {
                log.error(">>>>>>>>===send request failure", new IllegalStateException("response is null"));
                return null;
            }
            if (response.hasException()) {
                log.error(">>>>>>>>===response has exception", response.getException());
                return null;
            }
            // 返回结果
            return response.getResult();
        }

        /**
         * 发送请求到服务端
         * Send request rpc response.
         *
         * @param host    the host
         * @param port    the port
         * @param request the request
         * @return the rpc response
         */
        private RpcResponse sendRequest(String host, int port, RpcRequest request) {
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
                group.shutdownGracefully();
            }
        }
    }
}
