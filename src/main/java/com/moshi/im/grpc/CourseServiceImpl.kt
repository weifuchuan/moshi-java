package com.moshi.im.grpc

import com.moshi.common.model.Course
import com.moshi.im.common.CourseServiceGrpc
import com.moshi.im.common.CourseServiceGrpc.CourseServiceImplBase
import com.moshi.im.common.SubscribedCourseListReply
import com.moshi.im.common.SubscribedCourseListReq
import com.moshi.srv.v1.course.CourseService
import io.grpc.stub.StreamObserver
import com.moshi.im.common.Course as ImCourse

class CourseServiceImpl : CourseServiceImplBase() {
  val dao = Course().dao()
  val srv = CourseService()

  override fun subscribedCourseListBy(
    request: SubscribedCourseListReq?,
    responseObserver: StreamObserver<SubscribedCourseListReply>?
  ) {
    val subscribedCourses = srv.subscribedCourses(request?.accountId!!.toInt())
    responseObserver?.onNext(
      SubscribedCourseListReply
        .newBuilder()
        .addAllCourse(
          subscribedCourses.map {
            ImCourse
              .newBuilder()
              .setAccountId(it.getInt("accountId"))
              .setId(it.getInt("id"))
              .setName(it.getStr("name"))
              .setCourseType(it.getStr("courseType"))
              .setIntroduceImage(it.getStr("introduceImage")).build()
          }).build()
    )
    responseObserver?.onCompleted()
  }
}