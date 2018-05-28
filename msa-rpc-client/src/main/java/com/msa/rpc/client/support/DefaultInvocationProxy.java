package com.msa.rpc.client.support;

import com.google.common.net.HostAndPort;
import com.google.common.reflect.Reflection;
import com.msa.rpc.client.RpcClient;
import com.msa.rpc.common.bean.RpcRequest;
import com.msa.rpc.common.bean.RpcResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

/**
 * The type Default invocation proxy.
 */
@Slf4j
public class DefaultInvocationProxy implements InvocationProxy {

    /**
     * The Client.
     */
    private RpcClient client;

    /**
     * Instantiates a new Default invocation proxy.
     *
     * @param client the client
     */
    public DefaultInvocationProxy(RpcClient client) {
        this.client = client;
    }

    /**
     * New instance t.
     *
     * @param <T>    the type parameter
     * @param target the target
     * @return the t
     */
    public <T> T newInstance(final Class<T> target) {
        return Reflection.newProxy(target, new RpcInvocationHandler(target, client));
    }

    /**
     * New instance t.
     *
     * @param <T>            the type parameter
     * @param type           the type
     * @param interfaceClazz the interface clazz
     * @return the t
     */
    public <T> T newInstance(final Class<T> type, final Class interfaceClazz) {
        return Reflection.newProxy(type, new RpcInvocationHandler(interfaceClazz, client));
    }

    /**
     * The type Rpc invocation handler.
     *
     * @param <T> the type parameter
     */
    @Slf4j
    public static class RpcInvocationHandler<T> implements InvocationHandler {
        /**
         * The Interface clazz.
         */
        private Class<T> interfaceClazz;

        /**
         * The Client.
         */
        private RpcClient client;

        /**
         * Instantiates a new Rpc invocation handler.
         *
         * @param interfaceClazz the interface clazz
         * @param client         the client
         */
        public RpcInvocationHandler(Class<T> interfaceClazz, RpcClient client) {
            this.interfaceClazz = interfaceClazz;
            this.client = client;
        }

        /**
         * Invoke object.
         *
         * @param proxy  the proxy
         * @param method the method
         * @param args   the args
         * @return the object
         * @throws Throwable the throwable
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String serviceName = interfaceClazz.getName();
            String interfaceName = serviceName;
            RpcRequest request = RpcRequest.builder()
                    .requestId(UUID.randomUUID().toString())
                    .interfaceName(interfaceName)
                    .methodName(method.getName())
                    .parameterType(method.getParameterTypes())
                    .parameters(args)
                    .build();
            String hostAndPort = client.loadBalance(serviceName);
            log.debug(">>>>>>>>===discover service: {} ===> {}", serviceName, hostAndPort);
            if (StringUtils.isEmpty(hostAndPort)) {
                throw new RuntimeException(">>>>>>>>===server address is empty");
            }
            HostAndPort var0 = HostAndPort.fromString(hostAndPort);
            String host = var0.getHost();
            int port = var0.getPort();
            RpcResponse response = client.sendRequest(host, port, request);
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
    }
}
