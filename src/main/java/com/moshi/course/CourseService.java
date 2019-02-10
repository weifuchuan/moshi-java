package com.moshi.course;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moshi.common.model.Course;
import com.moshi.common.model.Issue;
import com.moshi.issue.IssueService;

import java.util.Date;
import java.util.List;

public class CourseService {

  public static final CourseService me = new CourseService();

  private Course dao = new Course().dao();

  public List<Course> findByAccountId(int id) {
    List<Course> courses = dao.find("select * from course where accountId = ?", id);
    return courses;
  }

  public Ret create(int accountId, String name, String introduce, int courseType) {
    Course course = new Course();
    course.setAccountId(accountId);
    course.setName(name);
    course.setIntroduce(introduce);
    course.setCourseType(courseType);
    course.setCreateAt(new Date().getTime());
    if (course.save()) {
      return Ret.ok("course", course);
    } else {
      return Ret.fail("msg", "创建课程失败");
    }
  }

  public Ret detail(int id, int accountId) {
    List<Record> articles = Db.find(Db.getSqlPara("article.find", id, accountId));
    List<Issue> issues = Issue.dao.find(Issue.dao.getSqlPara("issue.find", id));
    return Ret.ok("articles", articles).set("issues", issues);
  }

  public Ret update(int id, Kv items) {
    int updated = Db.update(Db.getSqlPara("course.update", Kv.by("items", items).set("id", id)));
    if (updated > 0) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }
}
