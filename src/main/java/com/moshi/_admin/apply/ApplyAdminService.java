package com.moshi._admin.apply;

import com.jfinal.kit.Ret;
import com.moshi.common.model.Account;
import com.moshi.common.model.Application;
import com.moshi.common.model.Course;
import com.moshi.login.LoginService;

public class ApplyAdminService {

  private Application dao = new Application().dao();

  public Ret pass(int id, String reply) {
    Application application = dao.findById(id);
    application.setStatus(Application.STATUS_SUCCESS);
    application.setReply(reply);
    if (application.update()) {
      if (application.getCategory() == Application.CATEGORY_TEACHER) {
        Account account = Account.dao.findById(application.getRefId());
        account.toTeacher();
        account.update();
        LoginService.me.reloadLoginAccount(account);
      } else if (application.getCategory() == Application.CATEGORY_COURSE_COLUMN) {
        Course course = Course.dao.findById(application.getRefId());
        course.toPassed();
        course.update();
      } else if (application.getCategory() == Application.CATEGORY_COURSE_VIDEO) {
        Course course = Course.dao.findById(application.getRefId());
        course.toPassed();
        course.update();
      }
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }

  public Ret reject(int id, String reply) {
    Application application = dao.findById(id);
    application.setStatus(Application.STATUS_FAIL);
    application.setReply(reply);
    if (application.update()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }
}
