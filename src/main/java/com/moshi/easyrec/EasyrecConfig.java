package com.moshi.easyrec;

import java.io.Serializable;

public class EasyrecConfig implements Serializable {
  private String serverUrl;
  private String tenantid;
  private String token;
  private String apikey;
  private String sessionid;

  public String getSessionid() {
    return sessionid;
  }

  public void setSessionid(String sessionid) {
    this.sessionid = sessionid;
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public String getTenantid() {
    return tenantid;
  }

  public void setTenantid(String tenantid) {
    this.tenantid = tenantid;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getApikey() {
    return apikey;
  }

  public void setApikey(String apikey) {
    this.apikey = apikey;
  }

  public EasyrecConfig clone() {
    EasyrecConfig config = new EasyrecConfig();
    config.apikey = apikey;
    config.serverUrl = serverUrl;
    config.sessionid = sessionid;
    config.tenantid = tenantid;
    config.token = token;
    return config;
  }
}
