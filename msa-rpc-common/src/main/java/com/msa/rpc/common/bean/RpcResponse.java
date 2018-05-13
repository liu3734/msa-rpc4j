package com.msa.rpc.common.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * @class:RpcResponse
 * @description:RPC响应bean
 * @author:sxp
 * @date:2018/4/15 10:29
 */
@Data
@Builder
@ToString
public class RpcResponse {

    /**
     * 请求编号，标识唯一请求
     */
    private String requestId;

    /**
     * 异常信息
     */
    Exception exception;

    /**
     * 响应结果
     */
    private Object result;

    /**
     * 是否带有异常
     */
    public boolean hasException() {
        return Objects.isNull(exception);
    }
}