package com.moshi.im.db

import com.moshi.im.common.model.MessageDetailModel
import com.moshi.im.common.model.RoomInfoModel
import com.moshi.im.common.payload.ChatReqPayload
import com.jfinal.kit.Kv
import io.lettuce.core.RedisFuture
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import org.tio.utils.page.Page

import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collector
import java.util.stream.Collectors

class DaoByRedis : IDao {

  @Throws(Exception::class)
  override fun sendChatMsg(payload: ChatReqPayload, from: String): Kv {
    val ret = Kv.create()
    //    RedisAsyncCommands<String, Object> async = R.async();
    //    async.setAutoFlushCommands(false);
    R.pipe { async ->
      var msg: MessageDetailModel? = null
      var room: RoomInfoModel? = null
      if (payload.type == 1) {
        val to = payload.to
        // save message
        val roomKey = Db.K.roomKey(from, to)
        msg = MessageDetailModel()
          .setMsgKey(nextId())
          .setFrom(from)
          .setTo(to)
          .setSendAt(Date().time)
          .setContent(payload.content)
        R.setHash(async, Db.K.message(msg!!.msgKey), msg)
        async.lpush(Db.K.messages(roomKey), msg.msgKey)
        // join room
        async.sadd(Db.K.joinedRooms(from), roomKey)
        async.sadd(Db.K.joinedRooms(payload.to), roomKey)
        // save room info
        room = RoomInfoModel().setType(1).setRoomKey(roomKey)
        R.setHash(async, Db.K.roomInfo(roomKey), room!!)
        async.sadd(Db.K.members(roomKey), from, payload.to)
        // add remind
        async.lpush(Db.K.remind(roomKey, to), msg.msgKey)
      } else {
        // TODO: 实现群聊
      }
      ret.set("msg", msg).set("room", room)
    }
    //    async.flushCommands();
    return ret
  }

  @Throws(Exception::class)
  override fun saveMessage(msg: MessageDetailModel, roomKey: String) {
    R.pipe { async ->
      R.setHash(async, Db.K.message(msg.msgKey), msg)
      async.lpush(Db.K.messages(roomKey), msg.msgKey)
    }
  }

  @Throws(Exception::class)
  override fun saveRoomInfo(room: RoomInfoModel, roomKey: String, members: Set<String>) {
    val sync = R.sync()
    if (!sync.hexists(Db.K.roomInfo(roomKey), "roomKey")) {
      R.pipeNotRetAsync { async ->
        R.setHash(async, Db.K.roomInfo(roomKey), room)
        async.sadd(Db.K.members(roomKey), *members.toTypedArray())
      }
    }
  }

  @Throws(Exception::class)
  override fun addRemind(msgKey: String, roomKey: String, accountId: String) {
    R.sync().lpush(Db.K.remind(roomKey, accountId), msgKey)
  }

  @Throws(Exception::class)
  override fun clearRemind(roomKey: String, accountId: String) {
    R.async().del(Db.K.remind(roomKey, accountId))
  }

  @Throws(Exception::class)
  override fun joinedRoomList(id: String): List<Map<*, *>> {
    val sync = R.sync()
    val roomKeyList = sync.smembers(Db.K.joinedRooms(id)).stream()
      .collect<List<Any>, Any>(Collectors.toList<Any>() as Collector<in Any, Any, List<Any>>?)
    var ret = R.pipe { async ->
      roomKeyList.forEach { roomKey ->
        async.llen(Db.K.remind(roomKey, id))
        async.lindex(Db.K.messages(roomKey), 0)
        async.hgetall(Db.K.roomInfo(roomKey))
        async.smembers(Db.K.members(roomKey))
      }
    }
    val remindCountList = ArrayList<Long>()
    val lastMsgKeyList = ArrayList<String>()
    val roomInfoList = ArrayList<RoomInfoModel>()
    val roomMembersSet = ArrayList<Set<Any>>()
    for (i in ret.indices) {
      if (i % 4 == 0) {
        remindCountList.add(ret[i] as Long)
      } else if (i % 4 == 1) {
        lastMsgKeyList.add(ret[i] as String)
      } else if (i % 4 == 2) {
        roomInfoList.add(RoomInfoModel.from(ret[i] as Map<*, *>))
      } else if (i % 4 == 3) {
        roomMembersSet.add(ret[i] as Set<Any>)
      }
    }
    ret = R.pipe { async ->
      for (msgKey in lastMsgKeyList) {
        async.hgetall(Db.K.message(msgKey))
      }
    }
    val lastMsgList = ret.stream().map { msg -> MessageDetailModel.from(msg as Map<*, *>) }
      .collect<List<MessageDetailModel>, MessageDetailModel>(Collectors.toList<MessageDetailModel>() as Collector<in MessageDetailModel, MessageDetailModel, List<MessageDetailModel>>)
    val i = AtomicInteger(0)
    return roomInfoList.stream()
      .map { room ->
        room.set("remindCount", remindCountList[i.get()])
        room.set("lastMsg", lastMsgList[i.get()])
        room.set("members", roomMembersSet[i.get()])
        i.getAndIncrement()
        room
      }
      .filter { room -> room["lastMsg"] != null }
      .sorted { _a, _b ->
        val a = _a["lastMsg"] as MessageDetailModel
        val b = _b["lastMsg"] as MessageDetailModel
        val sendAt1 = if (a.sendAt == null) 0 else a.sendAt
        val sendAt2 = if (b.sendAt == null) 0 else b.sendAt
        java.lang.Long.compare(sendAt2, sendAt1)
      }
      .collect<List<Map<*, *>>, Any>(Collectors.toList<Map<*, *>>() as Collector<in RoomInfoModel, Any, List<Map<*, *>>>?)
  }

  @Throws(Exception::class)
  override fun joinRoom(id: String, roomKey: String) {
    R.sync().sadd(Db.K.joinedRooms(id), roomKey)
  }

  @Throws(Exception::class)
  override fun joinRoom(idToRoomKey: Map<String, String>) {
    val async = R.async()
    async.setAutoFlushCommands(false)
    idToRoomKey.forEach { (id, roomKey) -> async.sadd(Db.K.joinedRooms(id), roomKey) }
    async.flushCommands()
  }

  @Throws(Exception::class)
  override fun getRoomInfo(roomKey: String, accountId: String): RoomInfoModel {
    val ret = R.pipe { async ->
      async.hgetall(Db.K.roomInfo(roomKey))
      async.llen(Db.K.remind(roomKey, accountId))
      async.lindex(Db.K.remind(roomKey, accountId), 0)
      async.smembers(Db.K.members(roomKey))
    }

    val map = ret[0] as Map<String, Any>
    val remindCount = ret[1] as Long
    val msgKey = ret[2] as String
    val members = ret[3] as Set<Any>

    val lastMsg = R.sync().hgetall(Db.K.message(msgKey))

    return RoomInfoModel.from(map)
      .set("remindCount", remindCount)
      .set("lastMsg", lastMsg)
      .set("members", members)
  }

  @Throws(Exception::class)
  override fun messagePage(roomKey: String, pageNumber: Int?, pageSize: Int?): Page<MessageDetailModel> {
    val async = R.async()
    async.setAutoFlushCommands(false)
    val totalCount = async.llen(Db.K.messages(roomKey))
    val msgKeyList = async.lrange(
      Db.K.messages(roomKey), ((pageNumber!! - 1) * pageSize!!).toLong(), (pageNumber * pageSize - 1).toLong()
    )
    async.flushCommands()
    val msgList = msgKeyList.get().stream()
      .map { msgKey -> async.hgetall(Db.K.message(msgKey)) }
      .collect<List<RedisFuture<Map<String, Any>>>, Any>(Collectors.toList<Any>() as Collector<in RedisFuture<MutableMap<String, Any>>, Any, List<RedisFuture<Map<String, Any>>>>?)
    async.flushCommands()
    return Page(
      msgList.stream()
        .map<MessageDetailModel> { msgF ->
          var ret: MessageDetailModel? = null
          try {
            ret = MessageDetailModel.from(msgF.get())
          } catch (e: InterruptedException) {
            e.printStackTrace()
          } catch (e: ExecutionException) {
            e.printStackTrace()
          }
          ret
        }
        .collect<List<MessageDetailModel>, Any>(Collectors.toList<Any>() as Collector<in MessageDetailModel, Any, List<MessageDetailModel>>?),
      pageNumber,
      pageSize,
      Math.toIntExact(totalCount.get()))
  }

  override fun nextId(): String {
    return R.nextId()
  }


  override fun onlineCountForUserId(userId: String): Int {
    val sync = R.sync()
    val count = sync.get(Db.K.onlineCount(userId)) as Int ?: 0
    return if (count < 0) {
      0
    } else count
  }

  override fun incrOnlineCount(userId: String) {
    R.async().incr(Db.K.onlineCount(userId))
  }

  override fun decrOnlineCount(userId: String) {
    R.async().decr(Db.K.onlineCount(userId))
  }

  override fun isOnline(userId: String): Boolean {
    return onlineCountForUserId(userId) > 0
  }
}
