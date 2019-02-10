package com.moshi.apply;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.SqlPara;
import com.moshi.common.model.Application;

import java.util.Date;
import java.util.List;

public class ApplyService {
  public static final ApplyService me = new ApplyService();

  private static final Application dao = Application.dao;

  public Ret commit(int accountId, int id, String title, String content, int category, int refId) {
    if (id == 0) {
      Application application = new Application();
      application.setAccountId(accountId);
      application.setCategory(category);
      application.setCreateAt(new Date().getTime());
      application.setTitle(title);
      application.setContent(content);
      application.setStatus(Application.STATUS_COMMIT);
      application.setRefId(refId);
      if (application.save()) {
        return Ret.ok("application", application);
      } else {
        return Ret.fail("msg", "申请提交失败");
      }
    } else {
      Application app = dao.findById(id);
      if (app != null && app.getAccountId() == accountId) {
        if (app.getStatus() == Application.STATUS_FAIL) {
          app.setTitle(title);
          app.setContent(content);
          app.setStatus(Application.STATUS_COMMIT);
          if (app.update()) {
            return Ret.ok();
          } else {
            return Ret.fail("msg", "申请修改失败");
          }
        } else {
          if (app.getStatus() == Application.STATUS_SUCCESS) {
            return Ret.fail("msg", "申请已成功");
          } else return Ret.fail("msg", "申请待审核");
        }
      } else {
        return Ret.fail("msg", "申请不存在");
      }
    }
  }

  public Ret cancel(int accountId, int id) {
    Application app = dao.findById(id);
    if (app != null && app.getAccountId() == accountId) {
      if (app.delete()) {
        return Ret.ok();
      } else {
        return Ret.fail("msg", "申请取消失败");
      }
    } else {
      return Ret.fail("msg", "申请不存在");
    }
  }

  public List<Application> findMy(int accountId, int category) {
    List<Application> list =
        dao.find(
            "select * from application where accountId = ? and category = ?", accountId, category);
    return list;
  }
}
