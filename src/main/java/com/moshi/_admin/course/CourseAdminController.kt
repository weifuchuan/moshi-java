package com.moshi._admin.course

import com.jfinal.aop.Inject
import com.jfinal.kit.Kv
import com.jfinal.kit.Ret
import com.moshi.common.controller.BaseController
import com.moshi.common.model.Course
import com.moshi.course.CourseService

class CourseAdminController : BaseController() {

  @Inject
  private lateinit var srv: CourseAdminService
  @Inject
  private lateinit var courseService: CourseService

  fun lock(id: Int) {
    val ret = srv.lock(id)
    renderJson(ret)
  }

  fun unlock(id: Int) {
    val ret = srv.unlock(id)
    renderJson(ret)
  }

  fun update() {
    val items = Kv.create()
    // offerTo discountedPrice price note introduceImage introduce name
    val name = getPara("name")
    val introduce = getPara("introduce")
    val introduceImage = getPara("introduceImage")
    val note = getPara("note")
    val price = getParaToInt("price")
    val discountedPrice = getParaToInt("discountedPrice")
    val offerTo = getParaToLong("offerTo")

    items
      .set("name", name)
      .set("introduce", introduce)
      .set("introduceImage", introduceImage)
      .set("note", note)
      .set("price", price)
      .set("discountedPrice", discountedPrice)
      .set("offerTo", offerTo)

    val ret = courseService.update(getParaToInt("id"), items)
    renderJson(ret)
  }

  fun publish(id: Int) {
    val ret = srv.publish(id)
    renderJson(ret)
  }

  fun unpublish(id: Int) {
    val ret = srv.unpublish(id)
    renderJson(ret)
  }

  fun addCourseType(courseId: Int, typeName: String) {
    val ret = srv.addCourseType(courseId, typeName)
    renderJson(ret)
  }

  fun delCourseType(id: String?, courseId: Int?, typeName: String?) {
    val ret = if (id != null) {
      srv.delCourseType(id)
    } else {
      srv.delCourseType(courseId!!, typeName!!)
    }
    renderJson(ret)
  }
}
