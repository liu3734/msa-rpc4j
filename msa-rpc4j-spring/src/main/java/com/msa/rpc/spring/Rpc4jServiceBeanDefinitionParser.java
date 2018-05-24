package com.msa.rpc.spring;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class Rpc4jServiceBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
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
        return ServiceBean.class;
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
        String clazz = element.getAttribute("interface");


    }
}
