package com.moshi.common.plugin;

import io.jboot.components.serializer.FastjsonSerializer;
import io.jboot.components.serializer.JbootSerializer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Letture {
  static RedisClient client;
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

  public static RedisClient getClient() {
    return client;
  }

  public static RedisClient c() {
    return client;
  }

  public static RedisAsyncCommands<String, Object> async() {
    return client.connect(codec).async();
  }

  public static RedisCommands<String, Object> sync() {
    return client.connect(codec).sync();
  }

  public static RedisReactiveCommands<String, Object> reactive() {
    return client.connect(codec).reactive();
  }

  public static StatefulRedisPubSubConnection<String, Object> pubSubConn() {
    return client.connectPubSub(codec);
  }
}
