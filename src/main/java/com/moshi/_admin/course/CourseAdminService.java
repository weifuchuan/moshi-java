package com.moshi._admin.course;

import com.jfinal.kit.Ret;
import com.moshi.common.model.Course;

public class CourseAdminService {
  private Course dao = new Course().dao();

  public Ret lock(Integer id) {
    Course course = dao.findById(id);
    course.toLock();
    return Ret.ok();
  }

  public Ret unlock(Integer id) {
    Course course = dao.findById(id);
    course.toUnlock();
    if (course.update()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }

  public Ret publish(int id) {
    Course course = dao.findById(id);
    if (!course.isPassed()) {
      return Ret.fail("msg", "未通过审核");
    }
    if (course.isLock()) {
      return Ret.fail("msg", "已锁定");
    }
    if (course.toPublish()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }

  public Ret unpublish(int id) {
    Course course = dao.findById(id);
    course.clearPublish();
    if (course.update()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }
}
