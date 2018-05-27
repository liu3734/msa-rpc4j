package com.msa.rpc.client.support;

/**
 * The type Rpc client factory.
 */
public class RpcClientFactory {

    /**
     * The Proxy.
     */
    private InvocationProxy proxy;

    /**
     * Instantiates a new Rpc client factory.
     *
     * @param proxy the proxy
     */
    public RpcClientFactory(InvocationProxy proxy) {
        this.proxy = proxy;
    }

    /**
     * New client t.
     *
     * @param <T>    the type parameter
     * @param target the target
     * @return the t
     */
    public <T> T newClient(Class<T> target) {
        return proxy.newInstance(target);
    }

    /**
     * New client t.
     *
     * @param <T>            the type parameter
     * @param type           the type
     * @param interfaceClazz the interface clazz
     * @return the t
     */
    public <T> T newClient(final Class<T> type, final Class interfaceClazz){
        return proxy.newInstance(type, interfaceClazz);
    }
}
