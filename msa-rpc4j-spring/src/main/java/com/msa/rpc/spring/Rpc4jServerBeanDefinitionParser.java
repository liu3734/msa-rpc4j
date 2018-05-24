package com.msa.rpc.spring;

import com.msa.rpc.server.RpcServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import java.util.Objects;

public class Rpc4jServerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
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
        return RpcServer.class;
    }

    /**
     * Parse the supplied {@link Element} and populate the supplied
     * {@link BeanDefinitionBuilder} as required.
     * <p>The default implementation delegates to the {@code doParse}
     * version without ParserContext argument.
     *
     * @param element       the XML element being parsed
     * @param parserContext the object encapsulating the current state of the parsing process
     * @param builder       used to define the {@code BeanDefinition}
     * @see #doParse(Element, BeanDefinitionBuilder)
     */
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String port = element.getAttribute("port");
        port = StringUtils.isEmpty(port) ? "8025" : port;
        builder.addPropertyValue("port", Integer.valueOf(port));
        String registry = element.getAttribute("registry");
        BeanDefinition registryBeanDefinition = parserContext.getRegistry().getBeanDefinition(registry);
        if (Objects.isNull(registryBeanDefinition)) {
            throw new IllegalArgumentException("registry center must required");
        }
        RuntimeBeanReference reference = new RuntimeBeanReference(registry);
        builder.addPropertyValue("registry", reference);
        String id = element.getAttribute("id");
        if (StringUtils.isEmpty(id)) {
            id = RpcServer.class.getCanonicalName();
        }
        parserContext.getRegistry().registerBeanDefinition(id, builder.getBeanDefinition());
    }

}
