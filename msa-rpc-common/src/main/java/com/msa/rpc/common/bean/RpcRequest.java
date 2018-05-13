package com.msa.rpc.common.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @class:RpcRequest
 * @description:RPC请求bean
 * @author:sxp
 * @date:2018/4/15 10:19
 */
@Data
@Builder
@ToString
public class RpcRequest {
    /**
     * 请求编号，用于标识一个请求
     */
    private String requestId;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 参数类型
     */
    private Class<?>[] parameterType;

    /**
     * 参数对象
     */
    private Object[] parameters;
}
