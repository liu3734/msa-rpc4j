package com.msa.rpc.spring;

import com.msa.api.regcovery.registry.ServiceRegistry;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class Rpc4jRegistryBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    /**
     * Determine the bean class corresponding to the supplied {@link Element}.
     * <p>Note that, for application classes, it is generally preferable to
     * override {@link #getBeanClassName} instead, in order to avoid a direct
     * dependence on the bean implementation class. The BeanDefinitionParser
     * and its NamespaceHandler can be used within an IDE plugin then, even
     * if the application classes are not available on the plugin's classpath.
     *
     * @param element the {@code Element} that is being parsed
     * @return the {@link Class} of the bean that is being defined via parsing
     * the supplied {@code Element}, or {@code null} if none
     * @see #getBeanClassName
     */
    @Override
    protected Class<?> getBeanClass(Element element) {
        return ServiceRegistry.class;
    }

    /**
     * Parse the supplied {@link Element} and populate the supplied
     * {@link BeanDefinitionBuilder} as required.
     * <p>The default implementation does nothing.
     *
     * @param element the XML element being parsed
     * @param builder used to define the {@code BeanDefinition}
     */
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {
        builder.addPropertyValue("zkAddress", element.getAttribute("registryAddress"));
    }
}
