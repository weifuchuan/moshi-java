package com.moshi.srv.v1.subscribe;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.moshi.common.socketio.Action;
import com.moshi.common.socketio.SocketIOServerEndPoint;
import com.moshi.common.socketio.SocketIOServerEventListener;
import io.jboot.Jboot;
import io.jboot.components.mq.JbootmqMessageListener;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubscribeConfirmServerEndPoint implements SocketIOServerEndPoint {
  @Override
  public String getNamespace() {
    return "/srv/v1/subscribe/confirm";
  }

  @Override
  public List<SocketIOServerEventListener<Action>> getEventListeners() {
    return Stream.of(
            new SocketIOServerEventListener<Action>() {
              @Override
              public String getEventName() {
                return "confirm";
              }

              @Override
              public Class<Action> getEventClass() {
                return Action.class;
              }

              @Override
              public DataListener<Action> getListener() {
                return (client, data, ackSender) -> {
                  if (data.getType().equals("verify")) {
                    JSONObject payload = JSON.parseObject(data.getPayload());
                    String key = payload.getString("key");
                    String id = payload.getString("id");
                    String idByStore = Jboot.getRedis().get("subscribe:" + key);
                    if (id.equals(idByStore)) {
                      client.sendEvent("verify", (Ret.ok()));
                    } else {
                      client.sendEvent("verify", (Ret.fail()));
                      client.disconnect();
                    }
                    Kv disposablePack = Kv.create();
                    Disposable disposable =
                        SubscribeController.SUBJECT.subscribe(
                            kv -> {
                              if (id.equals(kv.get("id"))) {
                                client.sendEvent("result", kv.get("ret"));
                                Observable.timer(3, TimeUnit.MINUTES)
                                    .subscribe(i -> client.disconnect());
                                ((Disposable) disposablePack.get("disposable")).dispose();
                              }
                            });
                    disposablePack.set("disposable", disposable);
                  }
                };
              }
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<ConnectListener> getConnectListeners() {
    return null;
  }

  @Override
  public List<DisconnectListener> getDisconnectListener() {
    return null;
  }
}
