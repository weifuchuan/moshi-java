package com.moshi.im.server.handler.actual

import com.alibaba.fastjson.JSONArray
import com.moshi.im.common.AccountBaseInfo
import com.moshi.im.common.Command
import com.moshi.im.common.ImPacket
import com.moshi.im.common.payload.OnlineNotifySubscribeReqPayload
import com.moshi.im.common.payload.OnlineOfflinePushPayload
import com.moshi.im.db.IDao
import com.moshi.im.kit.TioKit
import com.moshi.im.server.handler.*
import com.jfinal.kit.Kv
import io.reactivex.disposables.Disposable
import org.cliffc.high_scale_lib.NonBlockingHashMap
import org.cliffc.high_scale_lib.NonBlockingHashSet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext
import org.tio.core.Tio
import org.tio.utils.lock.ReadLockHandler
import org.tio.utils.lock.SetWithLock
import com.moshi.im.server.websocket.common.WsRequest

import java.util.Objects
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Predicate

@HandlerForCommand(Command.COMMAND_ONLINE_NOTIFY_SUBSCRIBE_REQ)
class OnlineNotifySubscribeHandler(dao: IDao) : BaseActualHandler(dao) {

  private val idToWS = NonBlockingHashMap<String, WhoSetAndSubscribed>(10000)

  @Throws(Exception::class)
  override fun handle(_packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any? {
    val packet = ImPacket.convert(_packet, OnlineNotifySubscribeReqPayload::class.java)

    val me = ctx.getAttribute("account") as AccountBaseInfo

    val who = packet.payload.who
    if (who == null || who.size == 0) return null

    val ws: WhoSetAndSubscribed = idToWS.compute(
      ctx.id
    ) { id, _ws ->
      if (_ws == null) {
        return@compute WhoSetAndSubscribed()
      }
      _ws
    }!!

    ws.whoSet.addAll(who.toJavaList(String::class.java))

    // if this context is unsubscribed
    if (!ws.subscribed.getAndSet(true)) {
      val packDisposable = Kv.create()
      val disposable = C.bus.subscribe { kv ->
        val type = kv["type"] as String? ?: return@subscribe
        // on handshaked
        if (type == "handshaked") {
          // somebody has online
          val context = kv["ctx"] as ChannelContext
          val account = context.getAttribute("account") as AccountBaseInfo

          // if account is subscribed by me
          if (ws.whoSet.contains(context.userid) && account.id != me.id) {
            if (dao.onlineCountForUserId(  account.id) <= 1)
            // push online notify to me
              TioKit.sendWsByText(
                ctx,
                ImPacket<OnlineOfflinePushPayload>()
                  .setCommand(Command.COMMAND_ONLINE_PUSH)
                  .setPayload(
                    OnlineOfflinePushPayload()
                      .setId(account.id)
                      .setNickName(account.nickName)
                      .setAvatar(account.avatar)
                  )
              )
          }
        }
        // on close
        if (type == "close") {
          // if closed context is this context
          if (ctx == kv["ctx"]) {
            packDisposable.getAs<Disposable>("disposable").dispose()
            idToWS.remove(ctx.id)
          } else {
            // somebody offline
            val context = kv["ctx"] as ChannelContext
            val account = context.getAttribute("account") as AccountBaseInfo

            if (ws.whoSet.contains(context.userid)) {
              if (dao.onlineCountForUserId( account.id) <= 1)
                TioKit.sendWsByText(
                  ctx,
                  ImPacket<OnlineOfflinePushPayload>()
                    .setCommand(Command.COMMAND_OFFLINE_PUSH)
                    .setPayload(
                      OnlineOfflinePushPayload()
                        .setId(account.id)
                        .setNickName(account.nickName)
                        .setAvatar(account.avatar)
                    )
                )
            }
          }
        }
      }
      packDisposable.set("disposable", disposable)
    }

    // for each new subscribed accounts, push online notify when account is online now
    who.parallelStream()
      .filter(Predicate<Any> { Objects.nonNull(it) })
      .forEach { id ->
        val isOnline = dao.isOnline( id as String)
        if (isOnline) {
          val contexts = Tio.getChannelContextsByUserid(ctx.groupContext, id)
          contexts?.handle(
            { ctxSet: Set<ChannelContext>? ->
              if (ctxSet != null && ctxSet.isNotEmpty()) {
                for (context in ctxSet) {
                  if (id == context.userid) {
                    val account = context.getAttribute("account") as AccountBaseInfo
                    TioKit.sendWsByText(
                      ctx,
                      ImPacket<OnlineOfflinePushPayload>()
                        .setCommand(Command.COMMAND_ONLINE_PUSH)
                        .setPayload(
                          OnlineOfflinePushPayload()
                            .setId(account.id)
                            .setNickName(account.nickName)
                            .setAvatar(account.avatar)
                        )
                    )
                  }
                }
              }
            } as ReadLockHandler<Set<ChannelContext>>)
        }
      }

    return null
  }

  private inner class WhoSetAndSubscribed internal constructor() {
    internal val whoSet = NonBlockingHashSet<String>()
    internal val subscribed = AtomicBoolean(false)
  }

  companion object {
    private val log = LoggerFactory.getLogger(OnlineNotifySubscribeHandler::class.java)
  }
}
