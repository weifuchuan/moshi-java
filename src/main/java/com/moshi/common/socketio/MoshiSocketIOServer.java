package com.moshi.common.socketio;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;

import com.moshi.common.MainConfig;
import jodd.io.findfile.ClassScanner;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

public class MoshiSocketIOServer {
  private SocketIOServer server;

  public void start(List<SocketIOServerEndPoint<Action>> endPoints) {
    Configuration conf = new Configuration();
    conf.setHostname(MainConfig.Companion.getP().get("socketio.host").trim());
    conf.setPort(Integer.parseInt(MainConfig.Companion.getP().get("socketio.port").trim()));
    conf.setPingTimeout(30000);
    server = new SocketIOServer(conf);
    for (SocketIOServerEndPoint<Action> point : endPoints) {

      SocketIONamespace ns = server.addNamespace(point.getNamespace());
      if (point.getEventListeners() != null)
        for (SocketIOServerEventListener<Action> listener : point.getEventListeners()) {
          ns.addEventListener(
              listener.getEventName(), listener.getEventClass(), listener.getListener());
        }
      List<ConnectListener> connectListeners = point.getConnectListeners();
      if (connectListeners != null) {
        for (ConnectListener listener : connectListeners) {
          ns.addConnectListener(listener);
        }
      }
      List<DisconnectListener> disconnectListeners = point.getDisconnectListener();
      if (disconnectListeners != null) {
        for (DisconnectListener listener : disconnectListeners) {
          ns.addDisconnectListener(listener);
        }
      }
    }
    server.start();
  }

  public void stop() {
    server.stop();
    // sleep 5 seconds for real stop the server
    try {
      Thread.sleep(1000 * 2);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
