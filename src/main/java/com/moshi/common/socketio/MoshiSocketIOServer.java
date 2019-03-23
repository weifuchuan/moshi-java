package com.moshi.common.socketio;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import io.jboot.Jboot;
import io.jboot.utils.ClassScanner;

import java.util.List;

public class MoshiSocketIOServer {
  private SocketIOServer server;

  public void start() {
    Configuration conf = new Configuration();
    conf.setHostname(Jboot.configValue("socketio.host").trim());
    conf.setPort(Integer.parseInt(Jboot.configValue("socketio.port").trim()));
    conf.setPingTimeout(30000);
    server = new SocketIOServer(conf);
    List<Class<SocketIOServerEndPoint>> classes =
        ClassScanner.scanSubClass(SocketIOServerEndPoint.class, true);
    for (Class<SocketIOServerEndPoint> cls : classes) {
      try {
        SocketIOServerEndPoint<Action> point = cls.newInstance();
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
        System.out.println("ADD SocketIOServerEventListener: " + cls.getName());
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    server.start();
  }

  public void stop() {
    server.stop();
    // sleep 5 seconds for real stop the server
    try {
      Thread.sleep(1000 * 5);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
