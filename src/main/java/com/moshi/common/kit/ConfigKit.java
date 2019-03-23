package com.moshi.common.kit;

import io.jboot.Jboot;

public class ConfigKit {
  public static final String HOST_PORT =
      Jboot.configValue("host").trim() + ":" + Jboot.configValue("port").trim();
}
