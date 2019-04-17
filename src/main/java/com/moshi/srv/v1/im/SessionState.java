package com.moshi.srv.v1.im;

import com.moshi.common.model.Account;
import com.moshi.common.plugin.Letture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

public class SessionState extends NonBlockingHashMap<String, Object> {

  private Account me = null;

  private final RedisAsyncCommands<String, Object> async = Letture.async();
  private final RedisCommands<String, Object> sync = Letture.sync();
  private final RedisReactiveCommands<String, Object> reactive = Letture.reactive();
  private final StatefulRedisPubSubConnection<String, Object> pubConn = Letture.pubConn();
  private final StatefulRedisPubSubConnection<String, Object> subConn = Letture.subConn();

  public SessionState() {
    super();
  }

  public Account getMe() {
    return me;
  }

  public void setMe(Account me) {
    this.me = me;
  }

  public RedisAsyncCommands<String, Object> getAsync() {
    return async;
  }

  public RedisCommands<String, Object> getSync() {
    return sync;
  }

  public RedisReactiveCommands<String, Object> getReactive() {
    return reactive;
  }

  public StatefulRedisPubSubConnection<String, Object> getPubConn() {
    return pubConn;
  }

  public StatefulRedisPubSubConnection<String, Object> getSubConn() {
    return subConn;
  }
}
