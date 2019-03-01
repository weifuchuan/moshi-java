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
}
