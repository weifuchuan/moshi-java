package com.moshi.course;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.moshi.apply.ApplyService;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.IsTeacherInterceptor;
import com.moshi.common.model.Application;
import com.moshi.common.model.Course;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/course")
public class CourseController extends BaseController {

  @Inject private CourseService srv;

  @Before(IsTeacherInterceptor.class)
  public void myByTeacher() {
    List<Course> courses = srv.findByAccountId(getLoginAccountId());
    renderJson(courses);
  }

  @Before(IsTeacherInterceptor.class)
  public void create() {
    String name = getPara("name");
    String introduce = getPara("introduce");
    int courseType = getParaToInt("courseType");
    Ret ret = srv.create(getLoginAccountId(), name, introduce, courseType);
    if (ret.isOk()) {
      Course course = (Course) (ret.get("course"));
      String title = getPara("title");
      String content = getPara("content");
      Ret commitRet =
          ApplyService.me.commit(
              getLoginAccountId(),
              0,
              title,
              content,
              courseType == Course.TYPE_COLUMN
                  ? Application.CATEGORY_COURSE_COLUMN
                  : Application.CATEGORY_COURSE_VIDEO,
              course.getId());
      if (commitRet.isOk()) {
        renderJson(
            Ret.ok("courseId", course.getId())
                .set("courseCreateAt", course.getCreateAt())
                .set("accountId", getLoginAccountId()));
      } else {
        course.delete();
        renderJson(Ret.fail("msg", "课程申请失败"));
      }
    } else {
      renderJson(ret);
    }
  }

  public void detail() {
    int id = getParaToInt("id");
    Ret ret = srv.detail(id, getLoginAccountId());
    renderJson(ret);
  }

  public void update() {
    Kv items = Kv.create();

    String name = getPara("name");
    if (name != null) items.set("name", name);

    String introduce = getPara("introduce");
    if (introduce != null) items.set("introduce", introduce);

    String introduceImage = getPara("introduceImage");
    if (introduceImage != null) items.set("introduceImage", introduceImage);

    //    Integer status = getParaToInt("status");
    //    if (status != null) items.set("status", status);

    int id = getParaToInt("id");

    Ret ret = srv.update(id, items);
    renderJson(ret);
  }
}
