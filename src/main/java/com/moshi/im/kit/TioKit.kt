package com.moshi.im.kit

import com.moshi.im.common.ImPacket
import com.moshi.im.common.ImPacketCoder
import com.moshi.im.server.websocket.common.WsResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.tio.cluster.TioClusterVo
import org.tio.core.ChannelContext
import org.tio.core.GroupContext
import org.tio.core.Tio
import org.tio.core.intf.Packet
import org.tio.utils.lock.SetWithLock

import java.util.Collections
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.stream.Collector
import java.util.stream.Collectors

object TioKit {
  private val log = LoggerFactory.getLogger(TioKit::class.java)

  fun sendWsByText(ctx: ChannelContext, packet: ImPacket<*>): Boolean {
    return Tio.send(ctx, WsResponse.fromText(ImPacketCoder.encodeToString(packet), "UTF-8"))!!
  }

  fun sendWSToUserIdByText(ctx: ChannelContext, userId: String, packet: ImPacket<*>): Boolean {
    return Tio.sendToUser(
      ctx.groupContext,
      userId,
      WsResponse.fromText(ImPacketCoder.encodeToString(packet), "UTF-8")
    )!!
  }

  fun notifyClusterForAll(groupContext: GroupContext, packet: Packet) {
    val tioClusterConfig = groupContext.getTioClusterConfig()
    val tioClusterVo = TioClusterVo(packet)
    tioClusterVo.isToAll = true
    tioClusterConfig.publish(tioClusterVo)
  }

  fun notifyClusterForAll(ctx: ChannelContext, packet: Packet) {
    notifyClusterForAll(ctx.groupContext, packet)
  }

  fun notifyClusterForAll(ctx: ChannelContext, packet: ImPacket<*>) {
    notifyClusterForAll(ctx, packImPacket(packet))
  }

  fun notifyClusterForAll(groupContext: GroupContext, packet: ImPacket<*>) {
    notifyClusterForAll(groupContext, packImPacket(packet))
  }

  fun packImPacket(packet: ImPacket<*>): WsResponse {
    return WsResponse.fromText(ImPacketCoder.encodeToString(packet), "UTF-8")
  }

}
