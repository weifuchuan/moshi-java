package com.moshi.srv.v1.im;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.jfinal.kit.Kv;
import com.moshi.common.socketio.Action;
import com.moshi.common.socketio.SocketIOServerEndPoint;
import com.moshi.common.socketio.SocketIOServerEventListener;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImServerEndPoint implements SocketIOServerEndPoint<Action> {
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
    return Stream.<ConnectListener>of(client -> {}).collect(Collectors.toList());
  }

  @Override
  public List<DisconnectListener> getDisconnectListener() {
    return Stream.<DisconnectListener>of(client -> {}).collect(Collectors.toList());
  }
}
