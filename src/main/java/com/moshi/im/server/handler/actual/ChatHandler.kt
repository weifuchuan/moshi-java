package com.moshi.im.server.handler.actual

import com.moshi.im.common.AccountBaseInfo
import com.moshi.im.common.Command
import com.moshi.im.common.ImPacket
import com.moshi.im.common.model.MessageDetailModel
import com.moshi.im.common.model.RoomInfoModel
import com.moshi.im.common.payload.ChatReqPayload
import com.moshi.im.common.payload.RemindPushPayload
import com.moshi.im.db.IDao
import com.moshi.im.kit.TioKit
import com.moshi.im.server.handler.BaseActualHandler
import com.moshi.im.server.handler.HandlerForCommand
import com.moshi.im.server.websocket.common.WsRequest
import com.jfinal.kit.Kv
import com.moshi.im.common.payload.ChatRespPayload
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext

@HandlerForCommand(Command.COMMAND_CHAT_REQ)
class ChatHandler(dao: IDao) : BaseActualHandler(dao) {

  override fun handle(_packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any? {
    val packet = ImPacket.convert(_packet, ChatReqPayload::class.java)
    try {
      val me = ctx.getAttribute("account") as AccountBaseInfo
      val payload = packet.payload
      // 一对一
      if (payload.type == 1) {
        val to = payload.to
        // not self to self
        if (ctx.userid == to) return null

        val ret = dao.sendChatMsg(payload, me.id)

        if (ret.isFail) return null

        val msg = ret.getAs<MessageDetailModel>("msg")

        val room = ret.getAs<RoomInfoModel>("room")

        val remind = ImPacket<RemindPushPayload>()
          .setCommand(Command.COMMAND_REMIND_PUSH)
          .setPayload(
            RemindPushPayload()
              .setMsgKey(msg.msgKey)
              .setRoomKey(room.roomKey)
              .setFrom(msg.from)
              .setTo(msg.to)
              .setContent(msg.content)
              .setSendAt(msg.sendAt)
              .setType(payload.type)
          )

        TioKit.sendWSToUserIdByText(ctx, me.id, remind)
        TioKit.sendWSToUserIdByText(ctx, to, remind)

      }
      // 群聊
      else if (payload.type == 2) {
        val to = payload.to
        val ret = dao.sendChatMsg(payload, to)
        val msg = ret.getAs<MessageDetailModel>("msg")
        val room = ret.getAs<RoomInfoModel>("room")
        val members = ret.getAs<Set<Any>>("members")

        val remind = ImPacket<RemindPushPayload>()
          .setCommand(Command.COMMAND_REMIND_PUSH)
          .setPayload(
            RemindPushPayload()
              .setMsgKey(msg.msgKey)
              .setRoomKey(room.roomKey)
              .setFrom(msg.from)
              .setTo(msg.to)
              .setContent(msg.content)
              .setSendAt(msg.sendAt)
              .setType(payload.type)
          )

        members.parallelStream().forEach { id ->
          TioKit.sendWSToUserIdByText(ctx, id.toString(), remind)
        }

        TioKit.sendWsByText(
          ctx,
          ImPacket<ChatRespPayload>(
            Command.COMMAND_CHAT_RESP,
            ChatRespPayload().setState("fail").setUuid(packet.payload.uuid)
          )
        )
      }
      return null
    } catch (e: Exception) {
      log.error("send fail: {}", e)
      TioKit.sendWsByText(
        ctx,
        ImPacket<ChatRespPayload>(
          Command.COMMAND_CHAT_RESP,
          ChatRespPayload().setState("ok").setUuid(packet.payload.uuid)
        )
      )
      return null
    }
  }

  companion object {
    private val log = LoggerFactory.getLogger(ChatHandler::class.java)
  }
}
