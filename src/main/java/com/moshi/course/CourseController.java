package com.moshi.course;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.IsTeacherInterceptor;
import com.moshi.common.model.Course;
import io.jboot.web.controller.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/course")
public class CourseController extends BaseController {

  @Inject
  private CourseService srv;

  @Before(IsTeacherInterceptor.class)
  public void myByTeacher(){
    List<Course> courses = srv.findByAccountId(getLoginAccountId());
    renderJson(courses);
  }
}
