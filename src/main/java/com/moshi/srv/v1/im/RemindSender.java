package com.moshi.srv.v1.im;

import com.jfinal.kit.Kv;

public interface RemindSender {
  void send(Kv remind);
}
