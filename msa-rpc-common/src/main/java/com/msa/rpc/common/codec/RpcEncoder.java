package com.msa.rpc.common.codec;

import com.msa.rpc.common.util.SerializationUTL;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * The type Rpc encoder.
 *
 * @class:RpcEncoder
 * @description:RPC编码器
 * @author:sxp
 * @date:2018/4/15 10 :48
 */
@AllArgsConstructor
public class RpcEncoder extends MessageToByteEncoder{
    /**
     * The Generic class.
     */
    private Class<?> genericClass;

    /**
     * 编码
     * Encode.
     *
     * @param channelHandlerContext the channel handler context
     * @param in                    the in
     * @param out                   the out
     * @throws Exception the exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = SerializationUTL.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
