package com.moshi.srv.v1.news;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Article;
import com.moshi.common.model.Course;
import com.moshi.srv.v1.article.ArticleService;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;

public class NewsController extends BaseController {
  public static final int COURSE_ID_FOR_NEWS = Article.COURSE_ID_FOR_NEWS;

  @Inject ArticleService srv;

  public void list(int pageNumber, int pageSize) {
    Page<Article> page = srv.list(COURSE_ID_FOR_NEWS, "desc", pageNumber, pageSize, null);
    renderJson(Ret.ok( "page", page));
  }

  public void index(int id) {
    Ret ret = srv.findById(id, getLoginAccountId());
    renderJson(ret);
  }
}
