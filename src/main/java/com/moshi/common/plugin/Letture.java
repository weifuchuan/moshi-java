package com.moshi.common.plugin;

import com.jfinal.kit.Kv;
import io.jboot.components.serializer.FstSerializer;
import io.jboot.components.serializer.JbootSerializer;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Letture {
  private static RedisClient client;
  private static RedisClient pubClient; // for pub
  private static RedisClient subClient; // for sub
  private static StatefulRedisConnection<String, Object> conn;

  private static final RedisCodec<String, Object> codec =
      new RedisCodec<String, Object>() {
        private final JbootSerializer serializer = new FstSerializer();

        @Override
        public String decodeKey(ByteBuffer bytes) {
          return StandardCharsets.UTF_8.decode(bytes).toString();
        }

        @Override
        public Object decodeValue(ByteBuffer buf) {
          byte[] array = new byte[buf.remaining()];
          buf.get(array);
          return serializer.deserialize(array);
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

  static void init(RedisURI uri) {
    client = RedisClient.create(uri);
    pubClient = RedisClient.create(uri);
    subClient = RedisClient.create(uri);

    conn = client.connect(codec);
  }

  static void close() {
    client.shutdown();
    pubClient.shutdown();
    subClient.shutdown();
  }

  public static RedisClient getClient() {
    return client;
  }

  public static RedisClient c() {
    return client;
  }

  public static StatefulRedisConnection<String, Object> connect() {
    return conn;
    //    return client.connect(codec);
  }

  public static RedisAsyncCommands<String, Object> async() {
    return connect().async();
  }

  public static RedisCommands<String, Object> sync() {
    return connect().sync();
  }

  public static RedisFuture<TransactionResult> multi(
      Consumer<RedisAsyncCommands<String, Object>> consumer)
      throws ExecutionException, InterruptedException {
    RedisAsyncCommands<String, Object> async = client.connect(codec).async();
    async.multi();
    consumer.accept(async);
    return async.exec();
  }

  public static RedisReactiveCommands<String, Object> reactive() {
    return connect().reactive();
  }

  public static StatefulRedisPubSubConnection<String, Object> pubConn() {
    return pubClient.connectPubSub(codec);
  }

  public static StatefulRedisPubSubConnection<String, Object> subConn() {
    return subClient.connectPubSub(codec);
  }

  public static List<RedisFuture<Boolean>> setHash(
      RedisAsyncCommands<String, Object> async, String key, Kv data) {
    List<RedisFuture<Boolean>> futures = new ArrayList<>();
    data.forEach(
        (k, v) -> {
          RedisFuture<Boolean> future = async.hset(key, String.valueOf(k), v);
          futures.add(future);
        });
    return futures;
  }

  //  private static final ConcurrentLinkedQueue<StatefulRedisConnection<String, Object>>
  //      CONNECTION_QUEUE = new ConcurrentLinkedQueue<>();
  //  private static final AtomicInteger index = new AtomicInteger(0);
  //
  //  public static StatefulRedisConnection<String, Object> getConnection() {
  //    if (CONNECTION_QUEUE.isEmpty()) {
  //      return conn;
  //    }
  //  }
  public static Future<StatefulRedisConnection<String, Object>> getConnection() {
    // TODO
    return null;
  }
}
