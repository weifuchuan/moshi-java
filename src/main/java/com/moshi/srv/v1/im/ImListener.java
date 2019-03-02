package com.moshi.srv.v1.im;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.listener.DataListener;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.moshi.common.model.Account;
import com.moshi.common.plugin.Letture;
import com.moshi.common.socketio.Action;
import io.lettuce.core.LettuceFutures;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.reactive.RedisReactiveCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.vertx.ext.web.impl.ConcurrentLRUCache;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.awt.SystemColor.info;

public class ImListener implements DataListener<Action> {
  private static final ConcurrentMap<Integer, UUID> idToSessionId = new ConcurrentHashMap<>();
  private static final ConcurrentMap<Integer, Account> accountCache =
      new ConcurrentLRUCache<>(10000);

  @Override
  public void onData(SocketIOClient client, Action data, AckRequest ackSender) throws Exception {
    SessionState state = ImServerEndPoint.onlineClients.get(client.getSessionId());
    RedisAsyncCommands<String, Object> async = state.getAsync();
    RedisCommands<String, Object> sync = state.getSync();
    // 验证登录
    if (data.getType().equals("verify")) {
      // payload: { key: [key string] }
      if (!verify(client, data, ackSender, async, sync)) return;
    }
    if (state.getMe() == null) {
      ackSender.sendAckData(Ret.fail("msg", "unlogged"));
      client.disconnect();
      return;
    }
    // 发送给单个用户
    if (data.getType().equals("sendToOne")) {
      // payload: { to: [other side id], message: [message string] }
      sendToOne(client, data);
    }
    // 发送到群聊
    if (data.getType().equals("sendToGroup")) {
      // TODO
    }
    // 加群
    if(data.getType().equals("joinGroup")){
      // TODO
    }
    // 退群
    if(data.getType().equals("leaveGroup")){
      // TODO
    }
    // 建群
    if(data.getType().equals("createGroup")){
      JSONObject payload = JSON.parseObject(data.getPayload());
      String name = payload.getString("name");
//      payload.getString()
    }
  }

  private boolean verify(
      SocketIOClient client,
      Action data,
      AckRequest ackSender,
      RedisAsyncCommands<String, Object> async,
      RedisCommands<String, Object> sync)
      throws InterruptedException, ExecutionException {
    SessionState state = ImServerEndPoint.onlineClients.get(client.getSessionId());
    JSONObject payload = JSON.parseObject(data.getPayload());
    String key = payload.getString("key");
    Account me = (Account) sync.get(key);
    if (me == null) {
      ackSender.sendAckData(Ret.fail("msg", "unlogged"));
      client.disconnect();
      return false;
    } else {
      state.setMe(me);
      Set<Object> roomKeys = sync.smembers(K.joinedRoomKeys(me.getId()));
      async.multi();
      List<RedisFuture<Map<String, Object>>> roomInfoFutures =
          roomKeys.stream()
              .map(roomKey -> async.hgetall(K.roomInfo((String) roomKey)))
              .collect(Collectors.toList());
      List<RedisFuture<Object>> lastMsgFutures =
          roomKeys.stream()
              .map(roomKey -> async.lindex(K.mq((String) roomKey), 0))
              .collect(Collectors.toList());
      List<RedisFuture<Object>> offlineRemindCountFutures =
          roomKeys.stream()
              .map(k -> async.get(K.offlineRemindCount(k)))
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
      List<Integer> offlineRemindCounts =
          offlineRemindCountFutures.stream()
              .map(
                  future -> {
                    try {
                      Object count = future.get();
                      if (count == null) return 0;
                      return (Integer) count;
                    } catch (InterruptedException | ExecutionException e) {
                      e.printStackTrace();
                    }
                    return 0;
                  })
              .collect(Collectors.toList());
      ackSender.sendAckData(
          Ret.ok("roomKeys", roomKeys)
              .set("roomInfos", roomInfos)
              .set("lastMsgs", lastMsgs)
              .set("offlineRemindCounts", offlineRemindCounts));
      subscribeRemind(client, roomKeys);
      StatefulRedisPubSubConnection<String, Object> subConn = state.getSubConn();
      subConn.addListener(
          new RemindListener(
              remind -> {
                client.sendEvent("remind", remind);
              }));
      idToSessionId.put(me.getId(), client.getSessionId());

      return true;
    }
  }

  private void subscribeRemind(SocketIOClient client, Set<Object> _roomKeys)
      throws InterruptedException, ExecutionException {
    SessionState state = ImServerEndPoint.onlineClients.get(client.getSessionId());
    Set<String> roomKeys = _roomKeys.stream().map(K::remindForRoom).collect(Collectors.toSet());
    if (!state.containsKey("subscribedRoomKeys")) {
      state.put("subscribedRoomKeys", new HashSet<String>());
    }
    Set<String> subscribedRoomKeys = (Set<String>) state.get("subscribedRoomKeys");

    System.out.printf("\t%d subscribed %s\n", state.getMe().getId(), subscribedRoomKeys);

    List<String> unsubscribed = new ArrayList<>();
    for (Object k : roomKeys) {
      if (!subscribedRoomKeys.contains(k.toString())) {
        unsubscribed.add(k.toString());
      }
    }
    if (unsubscribed.size() > 0) {
      StatefulRedisPubSubConnection<String, Object> subConn = state.getSubConn();
      RedisPubSubAsyncCommands<String, Object> subAsyncCommands = subConn.async();
      LettuceFutures.awaitAll(
          60,
          TimeUnit.SECONDS,
          unsubscribed.stream().map(subAsyncCommands::subscribe).toArray(RedisFuture[]::new));

      System.out.printf("\t%d subscribe %s\n", state.getMe().getId(), unsubscribed);

      subscribedRoomKeys.addAll(unsubscribed);
    }
  }

  private void subscribeRemind(int accountId, SocketIONamespace ns, Set<Object> roomKeys)
      throws InterruptedException, ExecutionException {
    UUID sessionId = idToSessionId.get(accountId);
    if (sessionId == null) return;
    SocketIOClient client = ns.getClient(sessionId);
    if (client == null) return;
    subscribeRemind(client, roomKeys);
  }

  private void sendToOne(SocketIOClient client, Action data)
      throws ExecutionException, InterruptedException {
    SessionState state = ImServerEndPoint.onlineClients.get(client.getSessionId());
    Account me = state.getMe();
    RedisAsyncCommands<String, Object> async = state.getAsync();
    RedisCommands<String, Object> sync = state.getSync();
    StatefulRedisPubSubConnection<String, Object> pubConn = state.getPubConn();
    JSONObject payload = JSON.parseObject(data.getPayload());
    int to = payload.getIntValue("to");
    String roomKey = K.roomKey(me.getId(), to);
    String message = payload.getString("message");
    long sendAt = new Date().getTime();

    System.out.printf("\t%d send to %d, roomKey is %s\n", me.getId(), to, roomKey);

    async.multi();
    Kv msg = D.msg(me, roomKey, message, sendAt);
    async.lpush(K.mq(roomKey), msg);
    async.sadd(K.joinedRoomKeys(me.getId()), roomKey);
    async.sadd(K.joinedRoomKeys(to), roomKey);
    if (!sync.hexists(K.roomInfo(roomKey), "type")) {
      async.sadd(K.members(roomKey), to, me.getId());
      Letture.setHash(async, K.roomInfo(roomKey), D.oneByOneRoomInfo(K.members(roomKey)));
    }
    async.exec().get();
    // 在线
    if (isOnline(to)) {
      System.out.printf("\t%d is online\n", to);

      subscribeRemind(to, client.getNamespace(), Collections.singleton(roomKey));
      RedisPubSubAsyncCommands<String, Object> pubAsyncCommands = pubConn.async();
      pubAsyncCommands
          .publish(K.remindForRoom(roomKey), msg)
          .thenAccept(
              ret -> System.out.printf("\tpublish %d to %d success: %d\n", me.getId(), to, ret));
    } else {
      // 离线
      System.out.printf("\t%d is offline\n", to);

      // async.lpush(K.offlineReminds(roomKey), msg);
      async.incr(K.offlineRemindCount(roomKey));
    }
  }

  private Account loadAccount(int id) {
    Account account = accountCache.get(id);
    if (account != null) {
      return account;
    }
    account = (Account) Letture.sync().get(K.account(id));
    if (account != null) {
      accountCache.put(id, account);
      return account;
    }
    return reloadAccount(id);
  }

  private Account reloadAccount(int id) {
    Account account = new Account().dao().findById(id);
    if (account != null) {
      account.removeSensitiveInfo();
      accountCache.put(id, account);
      Letture.async().set(K.account(id), account);
      return account;
    }
    return null;
  }

  private boolean isOnline(int accountId) {
    UUID sessionId = idToSessionId.get(accountId);
    if (sessionId != null) {
      SessionState state = ImServerEndPoint.onlineClients.get(sessionId);
      return state != null;
    }
    return false;
  }
}
