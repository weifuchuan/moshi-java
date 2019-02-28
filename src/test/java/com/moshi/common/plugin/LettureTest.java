package com.moshi.common.plugin;

import io.jboot.components.serializer.FastjsonSerializer;
import io.jboot.components.serializer.JbootSerializer;
import io.lettuce.core.codec.RedisCodec;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class LettureTest {
  private static final RedisCodec<String, Object> codec =
    new RedisCodec<String, Object>() {
      private final JbootSerializer serializer = new FastjsonSerializer();

      @Override
      public String decodeKey(ByteBuffer bytes) {
        return StandardCharsets.UTF_8.decode(bytes).toString();
      }

      @Override
      public Object decodeValue(ByteBuffer bytes) {
        return serializer.deserialize(bytes.array());
      }

      @Override
      public ByteBuffer encodeKey(String key) {
        return StandardCharsets.UTF_8.encode(key);
      }

      @Override
      public ByteBuffer encodeValue(Object value) {
        return ByteBuffer.wrap(serializer.serialize(value));
      }
    };

  @Test
  void serializer(){
    ByteBuffer 去你的吧 = codec.encodeValue("去你的吧");
    Object value = codec.decodeValue(去你的吧);
    assertEquals("去你的吧",value);
  }
}