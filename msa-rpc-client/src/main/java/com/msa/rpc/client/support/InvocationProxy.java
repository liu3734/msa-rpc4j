package com.msa.rpc.client.support;

/**
 * The interface Invocation proxy.
 */
public interface InvocationProxy {

    /**
     * New instance t.
     *
     * @param <T>    the type parameter
     * @param target the target
     * @return the t
     */
    <T> T newInstance(final Class<T> target);

    /**
     * New instance t.
     *
     * @param <T>            the type parameter
     * @param type           the type
     * @param interfaceClazz the interface clazz
     * @return the t
     */
    <T> T newInstance(final Class<T> type, final Class interfaceClazz);
}
