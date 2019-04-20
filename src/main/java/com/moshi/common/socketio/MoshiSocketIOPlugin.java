package com.moshi.common.socketio;

import com.corundumstudio.socketio.SocketIOServer;
import com.jfinal.plugin.IPlugin;

public class MoshiSocketIOPlugin implements IPlugin {
  private MoshiSocketIOServer server;

  @Override
  public boolean start() {
    server = new MoshiSocketIOServer();
    server.start();
    return true;
  }

  @Override
  public boolean stop() {
    server.stop();
    return true;
  }
}
