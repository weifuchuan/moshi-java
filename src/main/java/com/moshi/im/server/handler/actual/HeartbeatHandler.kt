package com.moshi.im.server.handler.actual

import com.moshi.im.common.Command
import com.moshi.im.common.ImPacket
import com.moshi.im.common.payload.HeartbeatPayload
import com.moshi.im.db.IDao
import com.moshi.im.kit.TioKit
import com.moshi.im.server.handler.BaseActualHandler
import com.moshi.im.server.handler.HandlerForCommand
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext
import com.moshi.im.server.websocket.common.WsRequest

@HandlerForCommand(Command.COMMAND_HEARTBEAT_REQ)
class HeartbeatHandler(dao: IDao) : BaseActualHandler(dao) {

  override fun handle(_packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any? {
    TioKit.sendWsByText(ctx, respPacket)
    return null
  }

  companion object {
    private val log = LoggerFactory.getLogger(HeartbeatHandler::class.java)

    val respPacket = ImPacket<HeartbeatPayload>()
      .setCommand(Command.COMMAND_HEARTBEAT_RESP)
      .setPayload(HeartbeatPayload())
  }
}
