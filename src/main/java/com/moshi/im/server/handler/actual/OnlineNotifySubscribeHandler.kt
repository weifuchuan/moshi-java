package com.moshi.im.server.handler.actual

import com.alibaba.fastjson.JSONArray
import com.moshi.im.common.payload.OnlineNotifySubscribeReqPayload
import com.moshi.im.common.payload.OnlineOfflinePushPayload
import com.moshi.im.db.IDao
import com.moshi.im.kit.TioKit
import com.moshi.im.server.handler.*
import com.jfinal.kit.Kv
import com.moshi.im.common.*
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
  private val stub = BaseActualHandler.feather.instance(AccountServiceGrpc.AccountServiceBlockingStub::class.java)

  @Throws(Exception::class)
  override fun handle(_packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any? {
    val packet = ImPacket.convert(_packet, OnlineNotifySubscribeReqPayload::class.java)

    val me = ctx.getAttribute("account") as AccountBaseInfo

    val who = packet.payload.who
    if (who == null || who.size == 0) return null

    val ws: WhoSetAndSubscribed = idToWS.compute(
      ctx.id
    ) { id, _ws ->
      _ws ?: WhoSetAndSubscribed()
    }!!

    ws.whoSet.addAll(who.toJavaList(String::class.java))

    // if this context is unsubscribed
    if (!ws.subscribed.getAndSet(true)) {
      val packDisposable = Kv.create()
      // val disposable = C.bus.subscribe { kv ->
      //   val type = kv["type"] as String? ?: return@subscribe
      //   // on handshaked
      //   if (type == "handshaked") {
      //     // somebody has online
      //     val context = kv["ctx"] as ChannelContext
      //     val account = context.getAttribute("account") as AccountBaseInfo
      //
      //     // if account is subscribed by me
      //     if (ws.whoSet.contains(context.userid) && account.id != me.id) {
      //       // push online notify to me
      //       if (dao.onlineCountForUserId(account.id) <= 1)
      //         TioKit.sendWsByText(
      //           ctx,
      //           ImPacket<OnlineOfflinePushPayload>()
      //             .setCommand(Command.COMMAND_ONLINE_PUSH)
      //             .setPayload(
      //               OnlineOfflinePushPayload()
      //                 .setId(account.id)
      //                 .setNickName(account.nickName)
      //                 .setAvatar(account.avatar)
      //             )
      //         )
      //     }
      //   }
      //   // on close
      //   if (type == "close") {
      //     // if closed context is this context
      //     if (ctx == kv["ctx"]) {
      //       packDisposable.getAs<Disposable>("disposable").dispose()
      //       idToWS.remove(ctx.id)
      //     } else {
      //       // somebody offline
      //       val context = kv["ctx"] as ChannelContext
      //       val account = context.getAttribute("account") as AccountBaseInfo
      //
      //       if (ws.whoSet.contains(context.userid)) {
      //         if (dao.onlineCountForUserId(account.id) <= 1)
      //           TioKit.sendWsByText(
      //             ctx,
      //             ImPacket<OnlineOfflinePushPayload>()
      //               .setCommand(Command.COMMAND_OFFLINE_PUSH)
      //               .setPayload(
      //                 OnlineOfflinePushPayload()
      //                   .setId(account.id)
      //                   .setNickName(account.nickName)
      //                   .setAvatar(account.avatar)
      //               )
      //           )
      //       }
      //     }
      //   }
      // }
      // packDisposable.set("disposable", disposable)

      val close1 = C.mq.onMessage<Kv>("online") { msg ->
        val account = msg.getAs<AccountBaseInfo>("account")
        val ctxId = msg.getAs<String>("id")
        val context = Tio.getChannelContextById(ctx.groupContext, ctxId) ?: return@onMessage
        if (ws.whoSet.contains(context.userid) && account.id != me.id) {
          // push online notify to me
          if (dao.onlineCountForUserId(account.id) <= 1)
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

      val close2 = C.mq.onMessage<Kv>("offline") { msg ->
        val account = msg.getAs<AccountBaseInfo>("account")
        val ctxId = msg.getAs<String>("id")
        val context = Tio.getChannelContextById(ctx.groupContext, ctxId) ?: return@onMessage

        // if closed context is this context
        if (ctx.id == context.id) {
          close1()
          packDisposable.getAs<() -> Unit>("close2")()
          idToWS.remove(ctx.id)
        } else {
          // somebody offline
          if (ws.whoSet.contains(context.userid)) {
            if (dao.onlineCountForUserId(account.id) <= 1)
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

      packDisposable.set("close2", close2)
    }

    // for each new subscribed accounts, push online notify when account is online now
    who.parallelStream()
      .filter(Objects::nonNull)
      .forEach { id ->
        val isOnline = dao.isOnline(id as String)
        if (isOnline) {
          C.accountIdToAccount.compute(id) { _, _account ->
            val account =
              if (_account != null) _account
              else {
                val reply = stub.fetchBaseInfo(
                  AccountBaseInfoReq.newBuilder().setId(id).build()
                )
                if (reply.code == Code.OK) {
                  val account = reply.account
                  account
                } else {
                  null
                }
              }
            if (account != null)
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
            account
          }

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

    private val idToWS = NonBlockingHashMap<String, WhoSetAndSubscribed>(10000)
  }
}
