package com.moshi.im.server.handler

import com.moshi.im.common.*
import com.moshi.im.common.payload.OnlineOfflinePushPayload
import com.moshi.im.kit.TioKit
import com.jfinal.kit.Kv
import com.jfinal.kit.StrKit
import com.moshi.im.common.AccountServiceGrpc.AccountServiceBlockingStub
import com.moshi.im.db.IDao
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext
import org.tio.core.Tio
import org.tio.http.common.HttpRequest
import org.tio.http.common.HttpResponse
import org.tio.utils.lock.ReadLockHandler
import org.tio.utils.lock.SetWithLock
import com.moshi.im.server.websocket.common.WsRequest
import com.moshi.im.server.websocket.server.handler.IWsMsgHandler
import org.tio.cluster.TioClusterConfig

import java.util.Collections
import java.util.HashSet

class MainHandler(
  private val accountService: AccountServiceBlockingStub,
  private val authService: AuthServiceGrpc.AuthServiceBlockingStub,
  private val realHandler: IActualHandler,
  private val dao :IDao
) : IWsMsgHandler {

  @Throws(Exception::class)
  override fun handshake(req: HttpRequest, resp: HttpResponse, ctx: ChannelContext): HttpResponse? {
    val id = req.getParam("id")
    val token = req.getParam("token")
    if (StrKit.isBlank(id)) {
      Tio.close(ctx, "id is blank")
      return null
    }
    if (StrKit.isBlank(token)) {
      Tio.close(ctx, "token is blank")
      return null
    }
    val authReply = authService.auth(AuthReq.newBuilder().setId(id).setToken(token).build())
    if (authReply.code != Code.OK) {
      Tio.close(ctx, authReply.msg)
      return null
    }
    Tio.bindUser(ctx, id)
    return resp
  }

  @Throws(Exception::class)
  override fun onAfterHandshaked(req: HttpRequest, resp: HttpResponse, ctx: ChannelContext) {
    log.info("user {} connected", ctx.userid)
    try {
      val accountBaseInfoReply = accountService.fetchBaseInfo(AccountBaseInfoReq.newBuilder().setId(ctx.userid).build())
      if (accountBaseInfoReply.code == Code.OK) {
        val account = accountBaseInfoReply.account
        ctx.setAttribute("account", account)
        C.bus.onNext(
          Collections.unmodifiableMap(
            Kv.create().set(Kv.by("type", "handshaked").set("ctx", ctx))
          )
        )
        dao.incrOnlineCount(ctx.userid)
      } else {
        Tio.close(ctx, "account not exists")
      }
    } catch (ex: Exception) {
      ex.printStackTrace()
      Tio.close(ctx, "grpc service exception")
    }

  }

  @Throws(Exception::class)
  override fun onBytes(req: WsRequest, bytes: ByteArray, ctx: ChannelContext): Any {
    val packet = ImPacketCoder.decode(bytes)
    return realHandler.handle(packet, req, ctx)
  }

  @Throws(Exception::class)
  override fun onText(req: WsRequest, text: String, ctx: ChannelContext): Any {
    val packet = ImPacketCoder.decodeFromString(text)
    return realHandler.handle(packet, req, ctx)
  }

  @Throws(Exception::class)
  override fun onClose(req: WsRequest, bytes: ByteArray, ctx: ChannelContext): Any? {
    log.info("user {} disconnected", ctx.userid)
    C.bus.onNext(
      Collections.unmodifiableMap(Kv.create().set(Kv.by("type", "close").set("ctx", ctx)))
    )
    dao.decrOnlineCount(ctx.userid)
    return null
  }

  companion object {
    private val log = LoggerFactory.getLogger(MainHandler::class.java)
  }
}
