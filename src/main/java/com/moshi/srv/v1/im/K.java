package com.moshi.srv.v1.im;

import cn.hutool.core.util.ArrayUtil;
import com.jfinal.kit.HashKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/** Key factory */
public class K {
  public static String joinedRoomKeys(int accountId) {
    return "im:joined:" + accountId;
  }

  public static String roomInfo(String roomKey) {
    return "im:room:" + roomKey;
  }

  public static String remindForRoom(Object roomKey) {
    return "im:remind:for:room:" + roomKey;
  }

  public static String mq(String roomKey) {
    return "im:room:mq:" + roomKey;
  }

  public static String roomKey(Integer... args) {
    List<Integer> list = Arrays.asList(args);
    list.sort(Comparator.comparingInt(x -> x));
    String key = list.stream().map(Object::toString).collect(Collectors.joining("-"));
    return HashKit.sha256(key).substring(0, 16);
  }

  public static String account(int id) {
    return "im:account:" + id;
  }

  public static String offlineReminds(String roomKey) {
    return "im:offline:reminds:" + roomKey;
  }
}
