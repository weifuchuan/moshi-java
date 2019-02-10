package com.moshi.common.kit;

import com.jfinal.kit.StrKit;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedisConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.protocol.RedisCommand;

public class RedisKit {
  private static final JbootRedisConfig config = Jboot.config(JbootRedisConfig.class);

  // redis://[password@]host [:port][/database][? [timeout=timeout[d|h|m|s|ms|us|ns]] [
  // &database=database] [&clientName=clientName]]
  private static RedisClient client =
      RedisClient.create(
          String.format(
              "redis://%s%s:%s%s",
              StrKit.isBlank(config.getPassword()) ? "" : config.getPassword() + "@",
              config.getHost(),
              config.getPort() == null ? 6379 : config.getPort(),
              config.getDatabase() == null ? "" : "/" + config.getDatabase()));

  private static StatefulRedisConnection conn = client.connect();

  private static RedisCommands cmd = conn.sync();

  public static RedisCommands cmd() {
    return cmd;
  }
}
