package com.moshi.srv.v1.article;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.UnlockInterceptor;
import com.moshi.common.model.Article;

import java.util.List;

public class ArticleController extends BaseController {
  @Inject ArticleService srv;

  public void index(int id) {
    Ret ret = srv.findById(id, getLoginAccountId());
    renderJson(ret);
  }

  public void trialReading(int courseId) {
    List<Article> articles = srv.trialReading(courseId);
    renderJson(articles);
  }

  public void list(int courseId, String cending, int pageNumber, int pageSize) {
    Page<Article> page = srv.list(courseId, cending, pageNumber, pageSize, getLoginAccountId());
    renderJson(page);
  }

  @Before(UnlockInterceptor.class)
  public void like(int id) {
    Ret ret = srv.like(id, getLoginAccountId());
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void comment(int id, String content ){
    Ret ret = srv.comment(id, content, getLoginAccountId());
    renderJson(ret);
  }
}
