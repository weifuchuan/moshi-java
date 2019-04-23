package com.moshi.im.common.payload;

import java.util.Map;

public class JoinGroupPayload extends BasePayload {

  
  public JoinGroupPayload setAccountId(Integer accountId) {
    set("accountId", accountId);
    return this;
  }

  
  public Integer getAccountId() {
    return (Integer)get("accountId");
  }

  
  public JoinGroupPayload setGroupId(Integer groupId) {
    set("groupId", groupId);
    return this;
  }

  
  public Integer getGroupId() {
    return (Integer)get("groupId");
  }


  public static JoinGroupPayload from(Map obj) {
    JoinGroupPayload model = new JoinGroupPayload();
    model.set(obj);
    return model;
  }
}
