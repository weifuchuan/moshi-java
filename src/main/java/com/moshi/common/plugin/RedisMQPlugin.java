package com.moshi.common.plugin;

import com.jfinal.plugin.IPlugin;
import com.moshi.common.mq.RedisMQ;
import io.lettuce.core.RedisURI;

public class RedisMQPlugin implements IPlugin {
  private static RedisMQ mq;

  public static RedisMQ getMq() {
    return mq;
  }

  private RedisURI uri;

  public RedisMQPlugin(RedisURI uri) {
    this.uri = uri;
  }

  @Override
  public boolean start() {
    mq = new RedisMQ(uri);
    return true;
  }

  @Override
  public boolean stop() {
    mq.stop();
    return true;
  }
}
