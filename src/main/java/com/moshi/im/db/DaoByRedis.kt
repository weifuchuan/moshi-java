package com.moshi.im.db

import com.moshi.im.common.model.MessageDetailModel
import com.moshi.im.common.model.RoomInfoModel
import com.moshi.im.common.payload.ChatReqPayload
import com.jfinal.kit.Kv
import com.jfinal.kit.Ret
import com.moshi.im.common.*
import io.lettuce.core.RedisFuture
import io.lettuce.core.api.async.RedisAsyncCommands
import io.lettuce.core.api.sync.RedisCommands
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.tio.utils.page.Page

import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Collector
import java.util.stream.Collectors

class DaoByRedis(
  private val courseService: CourseServiceGrpc.CourseServiceBlockingStub,
  private val redisson: RedissonClient
) : IDao {

  @Throws(Exception::class)
  override fun sendChatMsg(payload: ChatReqPayload, from: String): Ret {
    val sync = R.sync()
    val ret = Ret.ok()
    R.pipe { async ->
      val msg = MessageDetailModel()
        .setMsgKey(nextId())
        .setFrom(from)
        .setTo(payload.to)
        .setSendAt(Date().time)
        .setContent(payload.content)
        .setType(payload.type)
      var room: RoomInfoModel? = null
      val to = payload.to
      R.setHash(async, Db.K.message(msg.msgKey), msg)
      if (payload.type == 1) {
        // save message
        val roomKey = Db.K.roomKey(from, to)
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
        val roomKey = Db.K.roomKey(to)
        async.lpush(Db.K.messages(roomKey), msg.msgKey)
        async.sadd(Db.K.joinedRooms(from), roomKey)
        val members = sync.smembers(Db.K.members(roomKey))
        members.add(from)
        members.forEach { id ->
          async.lpush(Db.K.remind(roomKey, id), msg.msgKey)
        }
        ret.set("members", members)
      }
      ret.set("msg", msg).set("room", room)
    }
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
      R.pipe { async ->
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
    if (sync.get(Db.K.isFirstFetchJoinedRoom(id)) == "yes") {
      val reply =
        courseService.subscribedCourseListBy(SubscribedCourseListReq.newBuilder().setAccountId(id.toInt()).build())
      R.pipe { async ->
        reply.courseList.forEach { course ->
          setGroupRoomInfoIfNotExists(sync, Db.K.roomKey(course.id), course, async)
          async.sadd(Db.K.members(Db.K.roomKey(course.id)), id)
        }
      }
      sync.set(Db.K.isFirstFetchJoinedRoom(id), "yes")
    }
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

  override fun joinGroup(accountId: Int, groupId: Int): Ret {
    val roomKey = Db.K.roomKey(groupId.toString())

    val ret = Ret.ok()

    val sync = R.sync()

    val lock = redisson.getLock("lock:joinGroup:$accountId:$groupId")
    try {
      lock.lock(30, TimeUnit.SECONDS)
      if (!sync.sismember(Db.K.members(roomKey), accountId))
        R.pipe { async ->
          val reply = courseService.courseIfSubscribed(
            CourseIfSubscribedReq.newBuilder().setCourseId(groupId).setAccountId(accountId).build()
          )
          if (reply.code == Code.OK) {
            val course = reply.course
            setGroupRoomInfoIfNotExists(sync, roomKey, course, async)
            async.sadd(Db.K.members(roomKey), accountId)
          } else {
            ret.setFail().set("msg", "未订阅")
          }
        }
      else {
        ret.setFail().set("msg", "已加入")
      }
    } catch (ex: Exception) {
      log.error("join group fail: {}", ex)
      ret.setFail().set("msg", "出错")
    } finally {
      lock.unlock()
    }

    return if (ret.isOk) ret.set("members", sync.smembers(Db.K.members(roomKey))) else ret
  }

  private fun setGroupRoomInfoIfNotExists(
    sync: RedisCommands<String, Any>,
    roomKey: String,
    course: Course,
    async: RedisAsyncCommands<String, Any>?
  ) {
    if (!sync.hexists(Db.K.roomInfo(roomKey), "roomKey")) {
      val room =
        RoomInfoModel().setType(2).setRoomKey(roomKey).setName(course.name)
          .setId(course.id.toString())
      R.setHash(async, Db.K.roomInfo(roomKey), room)
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(DaoByRedis::class.java)
  }
}
