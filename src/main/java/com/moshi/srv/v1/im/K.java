package com.moshi.srv.v1.im;

import cn.hutool.core.util.ArrayUtil;
import com.jfinal.kit.HashKit;

import java.util.*;
import java.util.stream.Collectors;

/** Key factory */
public class K {
  public static String joinedRoomKeys(int accountId) {
    return "im:joined:" + accountId;
  }

  public static String roomInfo(Object roomKey) {
    return "im:room:" + roomKey;
  }

  public static String remindForRoom(Object roomKey) {
    return "im:remind:for:room:" + roomKey;
  }

  public static String mq(Object roomKey) {
    return "im:room:mq:" + roomKey;
  }

  private static Map<String, String> roomKeyCache = new HashMap<>();

  public static String roomKey(Integer... args) {
    List<Integer> list = Arrays.asList(args);
    list.sort(Comparator.comparingInt(x -> x));
    String key = list.stream().map(Object::toString).collect(Collectors.joining("-"));
    if (roomKeyCache.containsKey(key)) {
      return roomKeyCache.get(key);
    }
    String hashed = HashKit.sha256(key);
    StringBuilder ret = new StringBuilder().append('1');
    for (int i = 0; i < hashed.length() && ret.length() < 10; i++) {
      if ('0' <= hashed.charAt(i) && hashed.charAt(i) <= '9') ret.append(hashed.charAt(i));
    }
    if (ret.length() < 10) {
      for (int i = ret.length(); i < 10; i++) {
        ret.append('0');
      }
    }
    String roomKey = ret.toString();
    roomKeyCache.put(key, roomKey);
    return roomKey;
  }

  public static String account(Object id) {
    return "im:account:" + id;
  }

  public static String offlineRemindCount(Object roomKey) {
    return "im:offline:remind:count" + roomKey;
  }

  public static String members(Object roomKey) {
    return "im:room:members:" + roomKey;
  }
}
