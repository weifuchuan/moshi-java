package com.moshi.srv.v1.im;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.DataListener;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.moshi.common.model.Account;
import com.moshi.common.plugin.Letture;
import com.moshi.common.socketio.Action;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ImListener implements DataListener<Action> {
  //  private static ConcurrentMap<String, Kv>

  private Account me = null;

  private RedisAsyncCommands<String, Object> async = Letture.async();
  private RedisCommands<String, Object> sync = Letture.sync();
  private RedisReactiveCommands<String, Object> reactive = Letture.reactive();
  private StatefulRedisPubSubConnection<String, Object> subConn = Letture.pubSubConn();
  private RedisPubSubAsyncCommands<String, Object> subAsyncCommands = subConn.async();

  @Override
  public void onData(SocketIOClient client, Action data, AckRequest ackSender) throws Exception {
    if (data.getType().equals("verify")) {
      // payload: { key: [key string] }
      if (!verify(client, data, ackSender, async, sync)) return;
    }
    if (this.me == null) {
      ackSender.sendAckData(Ret.fail("msg", "unlogged"));
      client.disconnect();
      return;
    }
    if (data.getType().equals("sendToOne")) {
      // payload: { to: [other side id], message: [message string] }
      sendToOne(data);
    }
    if (data.getType().equals("send")) {
      // TODO
    }
  }

  private boolean verify(
      SocketIOClient client,
      Action data,
      AckRequest ackSender,
      RedisAsyncCommands<String, Object> async,
      RedisCommands<String, Object> sync)
      throws InterruptedException, ExecutionException {
    JSONObject payload = JSON.parseObject(data.getPayload());
    String key = payload.getString("key");
    Account me = (Account) sync.get(key);
    if (me == null) {
      ackSender.sendAckData(Ret.fail("msg", "unlogged"));
      client.disconnect();
      return false;
    } else {
      this.me = me;
      Set<Object> roomKeys = sync.smembers(KeyKit.joinedRoomKeys(me.getId()));
      async.multi();
      List<RedisFuture<Map<String, Object>>> roomInfoFutures =
          roomKeys.stream()
              .map(roomKey -> async.hgetall(KeyKit.roomInfo((String) roomKey)))
              .collect(Collectors.toList());
      List<RedisFuture<Object>> lastMsgFutures =
          roomKeys.stream()
              .map(roomKey -> async.lindex(KeyKit.mq((String) roomKey), 0))
              .collect(Collectors.toList());
      async.exec().get();
      List<Map<String, Object>> roomInfos =
          roomInfoFutures.stream()
              .map(
                  info -> {
                    try {
                      return info.get();
                    } catch (InterruptedException | ExecutionException e) {
                      e.printStackTrace();
                    }
                    return null;
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());
      List<Kv> lastMsgs =
          lastMsgFutures.stream()
              .map(
                  msg -> {
                    try {
                      return (Kv) msg.get();
                    } catch (InterruptedException | ExecutionException e) {
                      e.printStackTrace();
                    }
                    return Kv.create();
                  })
              .collect(Collectors.toList());
      ackSender.sendAckData(
          Ret.ok("roomKeys", roomKeys).set("roomInfos", roomInfos).set("lastMsgs", lastMsgs));
      subscribeRemind(client, roomKeys);
    }
    return true;
  }

  private void subscribeRemind(SocketIOClient client, Set<Object> roomKeys)
      throws InterruptedException, ExecutionException {
    subAsyncCommands.multi();
    roomKeys.stream()
        .map(k -> KeyKit.remindForRoom((String) k))
        .forEach(subAsyncCommands::subscribe);
    subAsyncCommands.exec().get();
    subConn.addListener(
        new RemindListener(
            remind -> {
              client.sendEvent("remind", remind);
            }));
  }

  private void sendToOne(Action data) throws ExecutionException, InterruptedException {
    JSONObject payload = JSON.parseObject(data.getPayload());
    int to = payload.getIntValue("to");
    String roomKey = KeyKit.roomKey(me.getId(), to);
    String message = payload.getString("message");
    long sendAt = new Date().getTime();
    async.multi();
    Kv msg =
        Kv.by("message", message)
            .set("roomKey", roomKey)
            .set("sendAt", sendAt)
            .set("sender", me.necessary());
    async.lpush(KeyKit.mq(roomKey), msg);
    async.sadd(KeyKit.joinedRoomKeys(me.getId()), roomKey);
    async.exec().get();
    subAsyncCommands.publish(KeyKit.remindForRoom(roomKey), msg);
  }
}
