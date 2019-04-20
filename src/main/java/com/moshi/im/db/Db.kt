package com.moshi.im.db

import com.jfinal.kit.HashKit
import java.util.*

class Db {
  object K {
    // room key 聊天室id：一对一聊天，按成员的id自然序排序后连接起来hash取前几位，以保持不变性
    fun roomKey(id1: String, id2: String): String {
      var id1 = id1
      var id2 = id2
      if (id1.compareTo(id2) > 0) {
        val t = id1
        id1 = id2
        id2 = t
      }
      val key = "$id1-$id2"
      return HashKit.md5(key).substring(0, 16)
    }

    // room key 聊天室id：群聊，群号
    fun roomKey(groupNumber: String): String {
      return groupNumber
    }

    // // 随机生成一个群号
    // fun groupNumber(): String {
    //   val str = "" + Date().toString() + Math.random()
    //   val hashed = HashKit.sha256(str)
    //   val sb = StringBuilder(10)
    //   for (i in 0 until hashed.length) {
    //     if ('0' <= hashed[i] && hashed[i] <= '9') {
    //       sb.append(hashed[i])
    //     }
    //     if (sb.length == 10) {
    //       break
    //     }
    //   }
    //   if (sb.length < 10) {
    //     for (i in sb.length..9) {
    //       sb.append('0')
    //     }
    //   }
    //   if (sb[0] == '0') {
    //     sb.setCharAt(0, '1')
    //   }
    //   return sb.toString()
    // }

    // account 已加入的 room | set
    fun joinedRooms(accountId: Any): String {
      return "im:joined:$accountId"
    }

    // room info | hash
    fun roomInfo(roomKey: Any): String {
      return "im:room:info:$roomKey"
    }

    // members of room | set
    fun members(roomKey: Any): String {
      return "im:room:members:$roomKey"
    }

    // message key list of room | list
    fun messages(roomKey: Any): String {
      return "im:room:messages:$roomKey"
    }

    // message detail | hash
    fun message(msgKey: Any): String {
      return "im:room:message:$msgKey"
    }

    // remind message key list of room | list
    fun remind(roomKey: Any, accountId: Any): String {
      return "im:room:remind:$roomKey:$accountId"
    }

    fun onlineCount(id: Any): String {
      return "im:onlineCount:$id"
    }
  }
}
