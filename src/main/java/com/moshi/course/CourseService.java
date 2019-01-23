package com.moshi.course;

import com.moshi.common.model.Course;

import java.util.List;

public class CourseService {

  public static final CourseService me =new CourseService();

  private Course dao = new Course().dao();

  public List<Course> findByAccountId(int id){
    List<Course> courses = dao.find("select * from course where accountId = ?", id);
    return courses;
  }

}
