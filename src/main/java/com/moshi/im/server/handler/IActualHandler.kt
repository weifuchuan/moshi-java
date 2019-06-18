package com.moshi.im.server.handler

import com.moshi.im.common.ImPacket
import org.tio.core.ChannelContext
import org.tio.websocket.common.WsRequest

interface IActualHandler {
  @Throws(Exception::class)
  fun handle(_packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any?
}
