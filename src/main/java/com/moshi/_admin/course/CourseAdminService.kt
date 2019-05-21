package com.moshi._admin.course

import com.jfinal.kit.Ret
import com.jfinal.plugin.activerecord.Db
import com.jfinal.plugin.activerecord.Db.*
import com.moshi.common.model.Course
import com.moshi.common.model.CourseType

open class CourseAdminService {
  private val dao = Course().dao()
  private val courseTypeDao = CourseType().dao()

  fun lock(id: Int?): Ret {
    val course = dao.findById(id)
    course.toLock()
    return Ret.ok()
  }

  fun unlock(id: Int?): Ret {
    val course = dao.findById(id)
    course.toUnlock()
    return if (course.update()) {
      Ret.ok()
    } else {
      Ret.fail("msg", "更新失败")
    }
  }

  fun publish(id: Int): Ret {
    val course = dao.findById(id)
    if (!course.isPassed) {
      return Ret.fail("msg", "未通过审核")
    }
    if (course.isLock) {
      return Ret.fail("msg", "已锁定")
    }
    return if (course.toPublish()) {
      Ret.ok()
    } else {
      Ret.fail("msg", "更新失败")
    }
  }

  fun unpublish(id: Int): Ret {
    val course = dao.findById(id)
    course.clearPublish()
    return if (course.update()) {
      Ret.ok()
    } else {
      Ret.fail("msg", "更新失败")
    }
  }

  fun addCourseType(courseId: Int, typeName: String): Ret {
    val courseType = CourseType()
    courseType.courseId = courseId;
    courseType.typeName = typeName
    val ok = courseType.save()
    return if (ok) {
      Ret.ok("id", courseType.id)
    } else {
      Ret.fail("msg", "保存失败")
    }
  }


  fun delCourseType(id: String): Ret {
    val ok = courseTypeDao.deleteById(id)
    return if (ok) {
      Ret.ok()
    } else {
      Ret.fail("msg", "删除失败")
    }
  }

  fun delCourseType(courseId: Int, typeName: String): Ret {
    val count = delete("delete from course_type where courseId = ? and typeName = ?", courseId, typeName)
    return if (count > 0) {
      Ret.ok()
    } else {
      Ret.fail("msg", "删除失败")
    }
  }
}
