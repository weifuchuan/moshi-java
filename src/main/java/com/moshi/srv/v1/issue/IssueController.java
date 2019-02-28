package com.moshi.srv.v1.issue;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.UnlockInterceptor;

public class IssueController extends BaseController {
  @Inject IssueService srv;

  public void index(int id) {
    Ret ret = srv.findById(id);
    renderJson(ret);
  }

  public void list(int courseId, String filter, int pageNumber, int pageSize) {
    filter = filter == null ? "open" : filter;
    assert filter.matches("(open)|(close)|(all)|(your)");
    Ret ret = srv.list(courseId, filter, pageNumber, pageSize);
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void create(int courseId, String title, String content) {
    Ret ret = srv.create(courseId, getLoginAccountId(), title, content);
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void close(int id, String reason) {
    Ret ret=srv.close(id, getLoginAccountId(), reason);
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void open(int id, String reason) {
    // TODO
  }

  public void listComment(int issueId, int pageNumber, int pageSize) {
    // TODO
  }

  @Before(UnlockInterceptor.class)
  public void comment(int id, String content) {
    Ret ret = srv.comment(id, content, getLoginAccountId());
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void updateComment(int id, String content) {
    // TODO
  }

  @Before(UnlockInterceptor.class)
  public void deleteComment(int id) {
    // TODO
  }
}
