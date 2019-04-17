package com.moshi.im.grpc

import com.moshi.common.kit.SqlKit
import com.moshi.common.model.Account
import com.moshi.im.common.*
import io.grpc.stub.StreamObserver
import java.util.concurrent.atomic.AtomicBoolean

class AccountServiceImpl : AccountServiceGrpc.AccountServiceImplBase() {
  private val dao = Account().dao()

  override fun fetchBaseInfo(
    request: AccountBaseInfoReq?,
    responseObserver: StreamObserver<AccountBaseInfoReply>?
  ) {
    val account = dao.findById(request!!.id)
    responseObserver!!.onNext(
      if (account != null) {
        AccountBaseInfoReply.newBuilder()
          .setAccount(
            AccountBaseInfo
              .newBuilder()
              .setId(account.id.toString())
              .setAvatar(account.avatar)
              .setNickName(account.nickName)
          ).setCode(
            Code.OK
          ).build()
      } else {
        AccountBaseInfoReply.newBuilder()
          .setCode(
            Code.FAIL
          ).build()
      }
    )
    responseObserver.onCompleted()
  }

  override fun fetchAccountListBaseInfo(
    request: FetchAccountListBaseInfoReq?,
    responseObserver: StreamObserver<FetchAccountListBaseInfoReply>?
  ) {
    val first = AtomicBoolean(true)
    val list = dao.find(
      """
      select * from `account`
      where ${request!!.idList.stream().reduce("", { ret, id ->
        val ret1 = if (first.get()) "" else " $ret or "
        first.set(false)
        " $ret1 id = $id "
      }, { ret, _ -> ret })}
      """.trimIndent()
    )
    val reply = if (list == null) {
      FetchAccountListBaseInfoReply.newBuilder().build()
    } else {
      val reply = FetchAccountListBaseInfoReply.newBuilder()
      list.forEach { account ->
        reply.addAccount(
          AccountBaseInfo
            .newBuilder()
            .setId(account.id.toString())
            .setAvatar(account.avatar)
            .setNickName(account.nickName)
        )
      }
      reply.build()
    }
    responseObserver?.onNext(reply)
    responseObserver?.onCompleted()
  }

  override fun fetchWaiters(request: WaitersReq?, responseObserver: StreamObserver<Waiters>?) {
    val list = dao.find(
      """
      select * from `account`
      where ${SqlKit.is1At("status", 4)} and ${SqlKit.is0At("status", 0)}
    """.trimIndent()
    )
    val reply = if (list == null) {
      Waiters.newBuilder().build()
    } else {
      val reply = Waiters.newBuilder()
      list.forEach { account ->
        reply.addAccount(
          AccountBaseInfo
            .newBuilder()
            .setId(account.id.toString())
            .setAvatar(account.avatar)
            .setNickName(account.nickName)
        )
      }
      reply.build()
    }
    responseObserver?.onNext(reply)
    responseObserver?.onCompleted()
  }
}
