package com.moshi.remind;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.moshi.login.LoginService;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.List;
import java.util.Map;

public class RemindServerEndpointConfigurator extends ServerEndpointConfig.Configurator {
  @Override
  public void modifyHandshake(
      ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
    Map<String, List<String>> headers = request.getHeaders();
    List<String> cookieList = headers.get("Cookie");
    Map<String, String> kv = MapUtil.newHashMap();
    if (CollUtil.isNotEmpty(cookieList)) {
      String cookieStr = cookieList.get(0);
      String[] cookieArray = cookieStr.split("; ");
      for (String cookie : cookieArray) {
        String[] cookieKv = cookie.split("=");
        kv.put(cookieKv[0], cookieKv[1]);
      }
    }
    String sessionId = kv.get(LoginService.sessionIdName);
    config.getUserProperties().put(LoginService.sessionIdName, sessionId);
  }
}
