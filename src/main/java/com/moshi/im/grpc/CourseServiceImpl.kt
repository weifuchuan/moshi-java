package com.moshi.im.grpc

import com.moshi.common.model.Course
import com.moshi.im.common.*
import com.moshi.im.common.CourseServiceGrpc.CourseServiceImplBase
import com.moshi.srv.v1.course.CourseService
import com.moshi.subscription.SubscriptionService
import io.grpc.stub.StreamObserver
import com.moshi.im.common.Course as ImCourse

class CourseServiceImpl : CourseServiceImplBase() {
  val dao = Course().dao()
  val srv = CourseService()
  val subSrv = SubscriptionService()

  override fun subscribedCourseListBy(
    request: SubscribedCourseListReq?,
    responseObserver: StreamObserver<SubscribedCourseListReply>?
  ) {
    val subscribedCourses = dao.findByCache(
      CourseService.cacheName, "subscribedCourses:" + request?.accountId!!, """
        select c.id,
          c.accountId,
          c.name,
          c.introduceImage,
          c.courseType,
          c.shortIntro,
          c.status
        from course c, subscription s
        where s.accountId = ?
          and s.subscribeType = 'course'
          and s.status = 1
          and s.refId = c.id
      """, request.accountId
    )

    responseObserver?.onNext(
      SubscribedCourseListReply
        .newBuilder()
        .addAllCourse(
          subscribedCourses.map {
            ImCourse
              .newBuilder()
              .setAccountId(it.accountId)
              .setId(it.id)
              .setName(it.name)
              .setCourseType(it.courseType)
              .setIntroduceImage(it.introduceImage)
              .setShortIntro(it.shortIntro)
              .build()
          }).build()
    )
    responseObserver?.onCompleted()
  }

  override fun courseIfSubscribed(
    request: CourseIfSubscribedReq?,
    responseObserver: StreamObserver<CourseIfSubscribedReply>?
  ) {
    val reply = if (subSrv.subscribedCourse(request!!.courseId, request.courseId)) {
      val course = dao.findById(request.courseId)
      if (course == null) {
        CourseIfSubscribedReply.newBuilder().setCode(Code.FAIL).build()
      } else {
        CourseIfSubscribedReply.newBuilder().setCode(Code.OK).setCourse(
          ImCourse
            .newBuilder()
            .setAccountId(course.accountId)
            .setId(course.id)
            .setName(course.name)
            .setCourseType(course.courseType)
            .setIntroduceImage(course.introduceImage).build()
        ).build()
      }
    } else {
      CourseIfSubscribedReply.newBuilder().setCode(Code.FAIL).build()
    }
    responseObserver!!.onNext(reply)
    responseObserver.onCompleted()
  }
}