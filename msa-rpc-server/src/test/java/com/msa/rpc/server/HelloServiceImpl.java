package com.msa.rpc.server;

/**
 * @class:HelloServiceImpl
 * @author:sxp
 * @date:2018/4/15 9:11
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String name) {
        return "hello" + name;
    }
}
