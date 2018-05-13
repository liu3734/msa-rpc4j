package com.msa.rpc.server.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @class:RpcService
 * @description:rpc服务注解类
 * @author:sxp
 * @date:2018/4/15 9:14
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {
    /**
     * 服务接口类
     */
    Class<?> value();
}
