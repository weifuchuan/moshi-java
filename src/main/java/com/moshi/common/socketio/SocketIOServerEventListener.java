package com.moshi.common.socketio;

import com.corundumstudio.socketio.listener.DataListener;

public interface SocketIOServerEventListener<T extends Action> {
  String getEventName();

  default Class<T> getEventClass(){
    return (Class<T>) Action.class;
  }

  DataListener<T> getListener();
}
