package com.moshi.im.server.handler.actual

import com.moshi.im.common.*
import com.moshi.im.common.payload.JoinGroupNotifyPushPayload
import com.moshi.im.common.payload.JoinGroupPayload
import com.moshi.im.db.IDao
import com.moshi.im.kit.TioKit
import com.moshi.im.server.handler.BaseActualHandler
import com.moshi.im.server.handler.C
import com.moshi.im.server.handler.HandlerForCommand
import com.moshi.im.server.websocket.common.WsRequest
import org.slf4j.LoggerFactory
import org.tio.core.ChannelContext

@HandlerForCommand(Command.COMMAND_JOIN_GROUP_REQ)
class JoinGroupHandler(dao: IDao) : BaseActualHandler(dao) {
  private val srv = feather.instance(AccountServiceGrpc.AccountServiceBlockingStub::class.java)

  override fun handle(_packet: ImPacket<*>, req: WsRequest, ctx: ChannelContext): Any? {
    val packet = ImPacket.convert(_packet, JoinGroupPayload::class.java)
    val payload = packet.payload
    val reply = srv.fetchBaseInfo(AccountBaseInfoReq.newBuilder().setId(payload.accountId.toString()).build())
    val account: AccountBaseInfo = reply.account
    if (reply.code == Code.OK) {
      C.accountIdToAccount[account.id]=account
    } else {
      log.error("account not exists: {}", payload.accountId)
      return null
    }
    val ret = dao.joinGroup(payload.accountId, payload.groupId)
    if (ret.isFail) {
      log.error("join group error: {}", ret["msg"])
      return null
    }
    val members = ret.getAs<Set<Any>>("members")

    members.parallelStream().forEach {
      val id = it.toString()
      TioKit.sendWSToUserIdByText(
        ctx,
        id,
        ImPacket<JoinGroupNotifyPushPayload>().setCommand(Command.COMMAND_JOIN_GROUP_NOTIFY_PUSH).setPayload(
          JoinGroupNotifyPushPayload().setAccountId(account.id.toInt()).setNickName(account.nickName).setAvatar(account.avatar).setGroupId(
            payload.groupId
          )
        )
      )
    }
    return null
  }

  companion object {
    private val log = LoggerFactory.getLogger(JoinGroupPayload::class.java)
  }
}