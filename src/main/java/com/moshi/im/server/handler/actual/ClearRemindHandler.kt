package com.moshi.im.server.handler.actual

import com.moshi.im.common.Command
import com.moshi.im.common.ImPacket
import com.moshi.im.common.payload.ClearRemindReqPayload
import com.moshi.im.db.IDao
import com.moshi.im.server.handler.BaseActualHandler
import com.moshi.im.server.handler.HandlerForCommand
import org.tio.core.ChannelContext
import org.tio.websocket.common.WsRequest

@HandlerForCommand(Command.COMMAND_CLEAR_REMIND_REQ)
class ClearRemindHandler(dao: IDao) : BaseActualHandler(dao) {

  @Throws(Exception::class)
  override fun handle(_packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any? {
    val packet = ImPacket.convert(_packet, ClearRemindReqPayload::class.java)
    val roomKey = packet.payload.roomKey
    dao.clearRemind(roomKey, ctx.userid)
    return null
  }
}
