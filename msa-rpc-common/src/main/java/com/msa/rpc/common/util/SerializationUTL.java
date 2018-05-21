package com.msa.rpc.common.util;

import com.google.common.collect.Maps;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.Objects;

/**
 * The type Serialization utl.
 */
@Slf4j
public class SerializationUTL {

    /**
     * The constant cachedSchema.
     */
    private static final Map<Class<?>, Schema<?>> cachedSchema = Maps.newConcurrentMap();

    /**
     * The constant objenesis.
     */
    private static final Objenesis objenesis = new ObjenesisStd(true);

    /**
     * Serialize byte [ ].
     *
     * @param <T> the type parameter
     * @param obj the obj
     * @return the byte [ ]
     */
    public static <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            log.error(">>>>>>>>>>===Class[{}] serialize fail", clazz.getName());
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化
     * Deserialize t.
     *
     * @param <T>   the type parameter
     * @param data  the data
     * @param clazz the clazz
     * @return the t
     */
    public static <T> T deserialize(byte[] data, Class<T> clazz) {
        try {
            T message = objenesis.newInstance(clazz);
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        }catch (Exception e) {
            log.error(">>>>>>>>>>===Class[{}] deserialize fail", clazz.getName());
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Gets schema.
     *
     * @param <T>   the type parameter
     * @param clazz the clazz
     * @return the schema
     */
    public static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(clazz);
        if (Objects.isNull(schema)) {
            schema = RuntimeSchema.createFrom(clazz);
            cachedSchema.put(clazz, schema);
        }
        return schema;
    }
}
