package com.moshi.remind;

import com.alibaba.fastjson.JSON;
import com.moshi.common.model.Account;
import com.moshi.login.LoginService;
import io.jboot.Jboot;

import javax.servlet.http.HttpSession;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;

@ServerEndpoint(value = "/websocket/remind", configurator = RemindServerEndpointConfigurator.class)
public class RemindWebSocketServer {
  @OnOpen
  public void onOpen(Session session, ServerEndpointConfig config) {
    String sessionId = (String) config.getUserProperties().get(LoginService.sessionIdName);
    if (sessionId == null) {
      try {
        session.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return;
    }
    Account account = LoginService.me.loginWithSessionId(sessionId);
    System.out.println(JSON.toJSONString(account));
    if (account == null) {
      try {
        session.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @OnClose
  public void onClose() {}

  @OnMessage
  public void onMessage(Session session, String msg) {
    Jboot.getMq();
  }
}
