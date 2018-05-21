package com.msa.rpc.server;

import com.google.common.collect.Maps;
import com.msa.api.regcovery.registry.ServiceRegistry;
import com.msa.rpc.common.bean.RpcRequest;
import com.msa.rpc.common.bean.RpcResponse;
import com.msa.rpc.common.codec.RpcDecoder;
import com.msa.rpc.common.codec.RpcEncoder;
import com.msa.rpc.server.annotation.RpcService;
import com.msa.rpc.server.handler.RpcServerReqHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.net.InetAddress;
import java.util.Map;

/**
 * The type Rpc server.
 */
@Slf4j
@Data
public class RpcServer implements ApplicationContextAware, InitializingBean {
    /**
     * The Port.
     */
    private int port;

    /**
     * The Handler map.
     */
    private final Map<String, Object> handlerMap = Maps.newHashMap();

    /**
     * The Registry.
     */
    private ServiceRegistry registry;

    /**
     * 服务扫描
     * Sets application context.
     *
     * @param applicationContext the application context
     * @throws BeansException the beans exception
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (CollectionUtils.isEmpty(serviceBeanMap)) {
            log.warn(">>>>>>>>>>>===warn:no service beans for rpc4j");
            return;
        }
        serviceBeanMap.forEach((beanName, serviceBean) -> {
            RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
            String serviceName = rpcService.value().getName();
            try {
                Class clazz = ClassUtils.getDefaultClassLoader().loadClass(serviceName);
                if (!clazz.isInterface()) {
                    throw new IllegalArgumentException(serviceName + " must be a interface");
                }
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(serviceName + " in classpath not find");
            }
            handlerMap.put(serviceName, serviceBean);
        });
    }

    /**
     * After properties set.
     *
     * @throws Exception the exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup childGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group, childGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    // 解码RPC请求
                    pipeline.addLast(new RpcDecoder(RpcRequest.class));
                    // 编码RPC响应
                    pipeline.addLast(new RpcEncoder(RpcResponse.class));
                    // 处理RPC请求
                    pipeline.addLast(new RpcServerReqHandler(handlerMap));
                }
            });
            ChannelFuture future = bootstrap.bind(port).sync();
            log.debug("server started, listening on {}", port);
            // 注册RPC服务地址
            String serviceAddr = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            handlerMap.forEach((serviceName, instance) -> {
                registry.registry(serviceName, serviceAddr);
                log.debug(">>>>>>>>>>===registry service: {} ==> {}", serviceName, serviceAddr);
            });
            // 释放资源
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error(">>>>>>>>>===server start exception", e);
        } finally {
            // 关闭RPC服务
            childGroup.shutdownGracefully();
            group.shutdownGracefully();
        }
    }
}
