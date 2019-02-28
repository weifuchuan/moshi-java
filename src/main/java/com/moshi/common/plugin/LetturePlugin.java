package com.moshi.common.plugin;

import com.jfinal.plugin.IPlugin;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

public class LetturePlugin implements IPlugin {
  private RedisURI uri;

  public LetturePlugin(RedisURI uri) {
    this.uri = uri;
  }

  @Override
  public boolean start() {
    try {
      Letture.client = RedisClient.create(uri);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  @Override
  public boolean stop() {
    Letture.client.shutdown();
    return true;
  }
}
