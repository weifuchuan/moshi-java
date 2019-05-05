package com.moshi.article;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.UnlockInterceptor;



public class ArticleController extends BaseController {
  @Inject private ArticleService srv;

  public void fetch() {
    int id = getParaToInt("id");
    Ret ret = srv.findAllUsefulData(id);
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void create() {
    Ret ret =
        srv.create(
            getPara("title"),
            getPara("summary"),
            getPara("content"),
            getParaToInt("courseId"),
            getParaToInt("audioId"));
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void update() {
    Kv items = Kv.create();
    String title = getPara("title");
    if (title != null) items.set("title", title);
    String summary = getPara("summary");
    if (summary != null) items.set("summary", summary);
    String content = getPara("content");
    if (content != null) items.set("content", content);
    Integer audioId = getParaToInt("audioId");
    if (audioId != null) items.set("audioId", audioId);
    Integer status = getParaToInt("status");
    if (status != null) items.set("status", status);

    int id = getParaToInt("id");

    Ret ret = srv.update(id, items);
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void comment() {
    int articleId = getParaToInt("articleId");
    String content = getPara("content");
    Integer replyTo = getParaToInt("replyTo");
    Ret ret = srv.comment(articleId, content, replyTo, getLoginAccountId());
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void removeComment() {
    Ret ret =
        srv.deleteComment(
            getParaToInt("articleId"), getParaToInt("commentId"), getLoginAccountId());
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void updateCommentStatus() {
    Ret ret =
        srv.updateCommentStatus(
            getParaToInt("commentId"),
            getParaToInt("status"),
            getParaToInt("articleId"),
            getLoginAccountId());
    renderJson(ret);
  }
}
