package com.moshi;

import com.jfinal.server.undertow.UndertowConfig;
import com.jfinal.server.undertow.UndertowServer;
import com.moshi.common.MainConfig;
import com.moshi.common.kit.ConfigKit;

public class App {

  public static void main(String[] args) {
    MainConfig.Companion.loadConfig();
    UndertowConfig undertowConfig =
        ConfigKit.createConfigObject(
            MainConfig.Companion.getP().getProperties(),
            UndertowConfig.class,
            "undertow",
            new Object[] {MainConfig.class});
    undertowConfig.setDevMode(MainConfig.Companion.getP().getBoolean("devMode", false));
    UndertowServer server = UndertowServer.create(undertowConfig);
    server.start();
  }
}
