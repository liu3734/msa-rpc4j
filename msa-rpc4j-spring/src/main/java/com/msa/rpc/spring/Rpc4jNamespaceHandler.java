package com.msa.rpc.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * The type Rpc 4 j namespace handler.
 */
public class Rpc4jNamespaceHandler extends NamespaceHandlerSupport {

    /**
     * Init.
     */
    @Override
    public void init() {
        registerBeanDefinitionParser("server", new Rpc4jServerBeanDefinitionParser());
        registerBeanDefinitionParser("registry", new Rpc4jRegistryBeanDefinitionParser());
        registerBeanDefinitionParser("service", new Rpc4jServiceBeanDefinitionParser());
    }
}
