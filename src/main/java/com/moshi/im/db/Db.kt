package com.moshi.im.db

class Db {
  object K {
    // room key 聊天室id：一对一聊天，按成员的id自然序排序后连接起来hash取前几位，以保持不变性
    fun roomKey(id1: String, id2: String): String {
      var id1 = id1
      var id2 = id2
      if (id1 > id2) {
        val t = id1
        id1 = id2
        id2 = t
      }
      val key = "$id1-$id2"
      return key
    }

    // room key 聊天室id：群聊，群号
    fun roomKey(groupId: Any): String {
      return groupId.toString()
    }

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

    // yes / no
    fun isFirstFetchJoinedRoom(id:Any) = "im:isFirstFetchJoinedRoom:$id"
  }
}
