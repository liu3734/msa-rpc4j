package com.msa.rpc.common.bean;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Objects;

/**
 * The type Rpc response.
 */
@Data
@Builder
@ToString
public class RpcResponse {

    /**
     * The Request id.
     */
    private String requestId;

    /**
     * The Exception.
     */
    Exception exception;

    /**
     * The Result.
     */
    private Object result;

    /**
     * Has exception boolean.
     *
     * @return the boolean
     */
    public boolean hasException() {
        return Objects.nonNull(exception);
    }
}