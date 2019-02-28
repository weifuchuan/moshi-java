package com.moshi._admin.news;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.moshi._admin.article.ArticleAdminService;
import com.moshi.article.ArticleService;
import com.moshi.common.controller.BaseController;
import com.moshi.common.kit.BitKit;
import com.moshi.common.model.Article;

public class NewsAdminController extends BaseController {
  public static final int COURSE_ID_FOR_NEWS = Article.COURSE_ID_FOR_NEWS;

  @Inject ArticleAdminService srv;
  @Inject ArticleService srv2;

  public void create(String title, String content, Integer audioId) {
    Ret ret = srv2.create(title, "", content, COURSE_ID_FOR_NEWS, audioId);
    renderJson(ret);
  }

  public void publish(int id) {
    int status = srv.status(id);
    BitKit.set(status, 1, 1);
    BitKit.set(status, 2, 1);
    renderJson(srv.updateStatus(id, status));
  }
}
