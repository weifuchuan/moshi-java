package com.moshi.im.common.payload;

import java.util.Map;

public class JoinGroupNotifyPushPayload extends BasePayload {

  
  public JoinGroupNotifyPushPayload setAccountId(Integer accountId) {
    set("accountId", accountId);
    return this;
  }

  
  public Integer getAccountId() {
    return (Integer)get("accountId");
  }

  
  public JoinGroupNotifyPushPayload setAvatar(String avatar) {
    set("avatar", avatar);
    return this;
  }

  
  public String getAvatar() {
    return (String)get("avatar");
  }

  
  public JoinGroupNotifyPushPayload setGroupId(Integer groupId) {
    set("groupId", groupId);
    return this;
  }

  
  public Integer getGroupId() {
    return (Integer)get("groupId");
  }

  
  public JoinGroupNotifyPushPayload setNickName(String nickName) {
    set("nickName", nickName);
    return this;
  }

  
  public String getNickName() {
    return (String)get("nickName");
  }


  public static JoinGroupNotifyPushPayload from(Map obj) {
    JoinGroupNotifyPushPayload model = new JoinGroupNotifyPushPayload();
    model.set(obj);
    return model;
  }
}
