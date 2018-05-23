package com.msa.rpc.spring;

import com.msa.rpc.server.RpcServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.context.support.ClassPathXmlApplicationContext;

@RunWith(JUnit4.class)
public class Rpc4jSpringXmlTest {

    @Test
    public void testSpring() {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:application.xml");
        RpcServer rpcServer = applicationContext.getBean(RpcServer.class);
        System.out.println(rpcServer == null);
        applicationContext.close();
    }
}
