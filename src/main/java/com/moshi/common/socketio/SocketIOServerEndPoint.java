package com.moshi.common.socketio;

import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import java.util.List;

public interface SocketIOServerEndPoint<T extends Action> {
  String getNamespace();

  List<SocketIOServerEventListener<T>> getEventListeners();

  List<ConnectListener> getConnectListeners();

  List<DisconnectListener> getDisconnectListener();
}
