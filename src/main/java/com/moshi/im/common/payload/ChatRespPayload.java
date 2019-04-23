package com.moshi.im.common.payload;

import java.util.Map;

public class ChatRespPayload extends BasePayload {

  // ok | fail
  public ChatRespPayload setState(String state) {
    set("state", state);
    return this;
  }

  // ok | fail
  public String getState() {
    return (String)get("state");
  }

  
  public ChatRespPayload setUuid(String uuid) {
    set("uuid", uuid);
    return this;
  }

  
  public String getUuid() {
    return (String)get("uuid");
  }


  public static ChatRespPayload from(Map obj) {
    ChatRespPayload model = new ChatRespPayload();
    model.set(obj);
    return model;
  }
}
