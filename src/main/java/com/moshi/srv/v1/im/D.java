package com.moshi.srv.v1.im;

import com.jfinal.kit.Kv;
import com.moshi.common.model.Account;

public class D {

  public static Kv msg(Account sender, String roomKey, String message, long sendAt) {
    return msg(sender.necessary(), roomKey, message, sendAt);
  }

  public static Kv msg(Kv sender, String roomKey, String message, long sendAt) {
    return Kv.by("message", message)
        .set("roomKey", roomKey)
        .set("sendAt", sendAt)
        .set("sender", sender);
  }

  public static Kv roomInfo(
      String type, // groupChat | oneByOne
      Kv info // necessary info
      ) {
    return Kv.by("type", type).set(info);
  }

  public static Kv groupChatRoomInfo(String name, String avatar, int master, String members) {
    return roomInfo(
        "groupChat",
        Kv.by("name", name).set("avatar", avatar).set("members", members).set("master", master));
  }

  public static Kv oneByOneRoomInfo(String members) {
    return roomInfo("oneByOne", Kv.create().set("members", members));
  }
}
