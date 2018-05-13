package com.msa.rpc.common.codec;

import com.msa.rpc.common.util.SerializationUTL;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * @class:RpcDecoder
 * @description:Rpc解码器
 * @author:sxp
 * @date:2018/4/15 11:22
 */
@AllArgsConstructor
public class RpcDecoder extends ByteToMessageDecoder {
    /**
     * The Generic class.
     */
    private Class<?> genericClass;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);
        out.add(SerializationUTL.deserialize(data, genericClass));
    }
}
