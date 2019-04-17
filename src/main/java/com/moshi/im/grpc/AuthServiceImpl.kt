package com.moshi.im.grpc

import com.moshi.common.plugin.Letture.sync
import com.moshi.im.ImController
import com.moshi.im.common.AuthReply
import com.moshi.im.common.AuthReply.newBuilder
import com.moshi.im.common.AuthReq
import com.moshi.im.common.AuthServiceGrpc
import com.moshi.im.common.Code
import io.grpc.stub.StreamObserver

class AuthServiceImpl : AuthServiceGrpc.AuthServiceImplBase() {
  override fun auth(request: AuthReq?, responseObserver: StreamObserver<AuthReply>?) {
    val id = request!!.id
    val token = request.token
    val sync = sync()

    val ok = sync.sismember(ImController.IM_AUTH + id, token)

    val replyBuilder = newBuilder()

    if (ok) {
      replyBuilder.code = Code.OK
    } else {
      replyBuilder.msg = "token invalid"
    }

    responseObserver!!.onNext(replyBuilder.build())
    responseObserver.onCompleted()
  }
}
