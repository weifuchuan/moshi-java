package com.moshi.section;

import com.jfinal.kit.Ret;
import com.moshi.common.model.Paragraph;
import com.moshi.common.model.Section;

import java.util.Date;

public class SectionService {
  public static final SectionService me = new SectionService();

  private Section dao = new Section().dao();
  private Paragraph pgDao = new Paragraph().dao();

  public Ret create(int courseId, int accountId, String title) {
    Section section = new Section();
    section.setCourseId(courseId);
    section.setAccountId(accountId);
    section.setTitle(title);
    section.setStatus(Section.STATUS_INIT);
    section.setCreateAt(new Date().getTime());
    if (section.save()) {
      return Ret.ok("section", section);
    } else {
      return Ret.fail("msg", "保存失败");
    }
  }

  public Ret delete(int id) {
    if (dao.deleteById(id)) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "删除失败");
    }
  }

  public Ret delete(int id, int accountId) {
    Section section = dao.findById(id);
    if (section.getAccountId() != accountId) {
      return Ret.fail("msg", "无权限");
    }
    if (section.delete()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "删除失败");
    }
  }
}
