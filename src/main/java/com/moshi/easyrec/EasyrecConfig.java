package com.moshi.easyrec;

public class EasyrecConfig {
  private String serverUrl;
  private String tenantid;
  private String token;
  private String apikey;

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
}
