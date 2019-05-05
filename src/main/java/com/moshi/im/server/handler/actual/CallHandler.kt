package com.moshi.im.server.handler.actual

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.moshi.im.common.*
import com.moshi.im.common.model.MessageDetailModel
import com.moshi.im.common.model.RoomInfoModel
import com.moshi.im.common.payload.CallReqPayload
import com.moshi.im.common.payload.CallRespPayload
import com.moshi.im.db.Db
import com.moshi.im.db.IDao
import com.moshi.im.kit.TioKit
import com.moshi.im.server.handler.BaseActualHandler
import com.moshi.im.server.handler.HandlerForCommand
import com.jfinal.kit.Kv
import com.jfinal.kit.Ret
import com.moshi.im.common.payload.ChatReqPayload
import com.moshi.im.server.handler.C
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext
import org.tio.core.Tio
import org.tio.utils.lock.SetWithLock
import org.tio.utils.page.Page
import com.moshi.im.server.websocket.common.WsRequest
import org.reflections.Reflections
import java.lang.reflect.Method
import java.util.stream.Collector
import java.util.stream.Collectors

/**
 * 来自客户端的调用请求，相当于： `
 * function [action](payload){
 * // ...
 * return ret
 * }
` *  客户端使用： `
 * const ret = await call(action, payload);
` *
 */
@HandlerForCommand(Command.COMMAND_CALL_REQ)
class CallHandler(dao: IDao) : BaseActualHandler(dao) {

  private val stub = BaseActualHandler.feather.instance(AccountServiceGrpc.AccountServiceBlockingStub::class.java)

  private val callableMethod = HashMap<String, Method>()

  init {
    val clz = CallHandler::class.java
    for (method in clz.declaredMethods) {
      if (method.parameterTypes.size == 3
        && method.parameterTypes[0].name == JSONObject::class.java.name
        && method.parameterTypes[1].name == WsRequest::class.java.name
        && method.parameterTypes[2].name == ChannelContext::class.java.name
        && method.returnType.name == Map::class.java.name
      ) {
        callableMethod[method.name] = method
      }
    }
  }

  @Throws(Exception::class)
  override fun handle(_packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any? {
    val packet = ImPacket.convert(_packet, CallReqPayload::class.java)
    val ret = handle(
      packet.payload.action,
      JSON.parseObject(packet.payload.payload),
      req,
      ctx
    )
    TioKit.sendWsByText(
      ctx,
      ImPacket<CallRespPayload>()
        .setCommand(Command.COMMAND_CALL_RESP)
        .setPayload(
          CallRespPayload()
            .setId(packet.payload.id)
            .setRet(JSON.toJSONString(ret))
        )
    )
    return null
  }

  @Throws(Exception::class)
  private fun handle(action: String, payload: JSONObject, req: WsRequest, ctx: ChannelContext): Map<*, *> {
    return if (callableMethod.containsKey(action)) {
      callableMethod[action]!!.invoke(this, payload, req, ctx) as Map<*, *>
    } else {
      Kv.create()
    }
  }

  private fun fetchMyAccountBaseInfo(payload: JSONObject, req: WsRequest, ctx: ChannelContext): Map<*, *> {
    val account = ctx.getAttribute("account") as AccountBaseInfo
    return Kv.by("id", account.id)
      .set("nickName", account.nickName)
      .set("avatar", account.avatar)
      .set("isOnline", true)
  }

  private fun fetchAccountBaseInfo(payload: JSONObject, req: WsRequest, ctx: ChannelContext): Map<*, *> {
    val reply = stub.fetchBaseInfo(
      AccountBaseInfoReq.newBuilder().setId(payload.getString("id")).build()
    )
    if (reply.code == Code.OK) {
      val account = reply.account
      C.accountIdToAccount[account.id] = account
      return Ret.ok(
        "account",
        Kv.by("id", account.id)
          .set("nickName", account.nickName)
          .set("avatar", account.avatar)
          .set("isOnline", dao.isOnline(account.id))
      )
    } else {
      return Ret.fail()
    }
  }

  private fun fetchJoinedRoomList(payload: JSONObject, req: WsRequest, ctx: ChannelContext): Map<*, *> {
    val roomList = dao.joinedRoomList(ctx.userid)
    return Kv.by("roomList", roomList)
  }

  private fun fetchRoomInfo(payload: JSONObject, req: WsRequest, ctx: ChannelContext): Map<*, *> {
    val roomKey: String
    if (payload.getIntValue("type") == 1) {
      roomKey = Db.K.roomKey(ctx.userid, payload.getString("to"))
    } else {
      roomKey = Db.K.roomKey(payload.getString("to"))
    }
    return dao.getRoomInfo(roomKey, ctx.userid)
  }

  private fun fetchWaiters(payload: JSONObject, req: WsRequest, ctx: ChannelContext): Map<*, *> {
    val waiters = stub.fetchWaiters(WaitersReq.newBuilder().build())
    val accountList = waiters.accountList
    return Kv.by(
      "waiterList",
      accountList.stream()
        .map { account ->
          Kv.by("id", account.id)
            .set("nickName", account.nickName)
            .set("avatar", account.avatar)
            .set("isOnline", dao.isOnline(account.id))
        }
        .collect<List<Kv>, Any>(Collectors.toList<Any>() as Collector<in Kv, Any, List<Kv>>))
  }

  private fun fetchMessagePage(payload: JSONObject, req: WsRequest, ctx: ChannelContext): Map<*, *> {
    val roomKey = payload.getString("roomKey")
    val pageNumber = payload.getInteger("pageNumber")
    val pageSize = payload.getInteger("pageSize")
    val messagePage = dao.messagePage(roomKey, pageNumber, pageSize)
    return Kv.by("page", messagePage)
  }

  private fun fetchAccountListBaseInfo(payload: JSONObject, req: WsRequest, ctx: ChannelContext): Map<*, *> {
    val idList = payload.getJSONArray("idList")
    val reply = stub.fetchAccountListBaseInfo(
      FetchAccountListBaseInfoReq.newBuilder()
        .addAllId(idList.toJavaList(String::class.java))
        .build()
    )
    val accountList = reply.accountList
    return Kv.by(
      "accountList",
      accountList.stream()
        .map { account ->
          Kv.by("id", account.id)
            .set("nickName", account.nickName)
            .set("avatar", account.avatar)
        }
        .toArray())
  }

  companion object {
    private val log = LoggerFactory.getLogger(CallHandler::class.java)
  }
}

/*


    when (action) {
      "fetchMyAccountBaseInfo" -> {
        val account = ctx.getAttribute("account") as AccountBaseInfo
        return Kv.by("id", account.id)
          .set("nickName", account.nickName)
          .set("avatar", account.avatar)
          .set("isOnline", true)
      }
      "fetchAccountBaseInfo" -> {
        val reply = stub.fetchBaseInfo(
          AccountBaseInfoReq.newBuilder().setId(payload.getString("id")).build()
        )
        if (reply.code == Code.OK) {
          val account = reply.account
          C.accountIdToAccount[account.id] = account
          return Ret.ok(
            "account",
            Kv.by("id", account.id)
              .set("nickName", account.nickName)
              .set("avatar", account.avatar)
              .set("isOnline", dao.isOnline(account.id))
          )
        } else {
          return Ret.fail()
        }
      }
      "fetchJoinedRoomList" -> {
        val roomList = dao.joinedRoomList(ctx.userid)
        return Kv.by("roomList", roomList)
      }
      "fetchRoomInfo" -> {
        val roomKey: String
        if (payload.getIntValue("type") == 1) {
          roomKey = Db.K.roomKey(ctx.userid, payload.getString("to"))
        } else {
          roomKey = Db.K.roomKey(payload.getString("to"))
        }
        return dao.getRoomInfo(roomKey, ctx.userid)
      }
      "fetchWaiters" -> {
        val waiters = stub.fetchWaiters(WaitersReq.newBuilder().build())
        val accountList = waiters.accountList
        return Kv.by(
          "waiterList",
          accountList.stream()
            .map { account ->
              Kv.by("id", account.id)
                .set("nickName", account.nickName)
                .set("avatar", account.avatar)
                .set("isOnline", dao.isOnline(account.id))
            }
            .collect<List<Kv>, Any>(Collectors.toList<Any>() as Collector<in Kv, Any, List<Kv>>))
      }
      "fetchMessagePage" -> {
        val roomKey = payload.getString("roomKey")
        val pageNumber = payload.getInteger("pageNumber")
        val pageSize = payload.getInteger("pageSize")
        val messagePage = dao.messagePage(roomKey, pageNumber, pageSize)
        return Kv.by("page", messagePage)
      }
      "fetchAccountListBaseInfo" -> {
        val idList = payload.getJSONArray("idList")
        val reply = stub.fetchAccountListBaseInfo(
          FetchAccountListBaseInfoReq.newBuilder()
            .addAllId(idList.toJavaList(String::class.java))
            .build()
        )
        val accountList = reply.accountList
        return Kv.by(
          "accountList",
          accountList.stream()
            .map { account ->
              Kv.by("id", account.id)
                .set("nickName", account.nickName)
                .set("avatar", account.avatar)
            }
            .toArray())
      }
    }
    return Kv.create()
*/