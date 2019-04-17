package com.moshi.srv.v1.im;

import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.moshi.common.socketio.Action;
import com.moshi.common.socketio.SocketIOServerEndPoint;
import com.moshi.common.socketio.SocketIOServerEventListener;
import org.cliffc.high_scale_lib.NonBlockingHashMap;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImServerEndPoint implements SocketIOServerEndPoint<Action> {
  static final ConcurrentMap<UUID, SessionState> onlineClients = new NonBlockingHashMap<>();

  @Override
  public String getNamespace() {
    return "/srv/v1/im";
  }

  @Override
  public List<SocketIOServerEventListener<Action>> getEventListeners() {
    return Stream.<SocketIOServerEventListener<Action>>of(
            new SocketIOServerEventListener<Action>() {
              @Override
              public String getEventName() {
                return "im";
              }

              @Override
              public DataListener<Action> getListener() {
                return new ImListener();
              }
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<ConnectListener> getConnectListeners() {
    return Stream.<ConnectListener>of(
            client -> {
              onlineClients.put(client.getSessionId(), new SessionState());
            })
        .collect(Collectors.toList());
  }

  @Override
  public List<DisconnectListener> getDisconnectListener() {
    return Stream.<DisconnectListener>of(
            client -> {
              onlineClients.remove(client.getSessionId());
            })
        .collect(Collectors.toList());
  }
}
