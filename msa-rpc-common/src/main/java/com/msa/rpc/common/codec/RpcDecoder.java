package com.msa.rpc.common.codec;

import com.msa.rpc.common.util.SerializationUTL;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * The type Rpc decoder.
 */
@AllArgsConstructor
public class RpcDecoder extends ByteToMessageDecoder {
    /**
     * The Generic class.
     */
    private Class<?> genericClass;

    /**
     * Decode.
     *
     * @param channelHandlerContext the channel handler context
     * @param in                    the in
     * @param out                   the out
     * @throws Exception the exception
     */
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
