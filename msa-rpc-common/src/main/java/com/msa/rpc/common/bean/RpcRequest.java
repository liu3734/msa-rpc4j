package com.msa.rpc.common.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * The type Rpc request.
 */
@Data
@Builder
@ToString
public class RpcRequest {
    /**
     * The Request id.
     */
    private String requestId;

    /**
     * The Interface name.
     */
    private String interfaceName;

    /**
     * The Method name.
     */
    private String methodName;

    /**
     * The Parameter type.
     */
    private Class<?>[] parameterType;

    /**
     * The Parameters.
     */
    private Object[] parameters;
}
