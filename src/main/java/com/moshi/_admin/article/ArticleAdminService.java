package com.moshi._admin.article;

import com.jfinal.kit.Ret;
import com.moshi.common.kit.BitKit;
import com.moshi.common.model.Article;

import java.util.Date;

public class ArticleAdminService {
  private Article dao = new Article().dao();

  public int status(int id){
    Article article = dao.findFirst("select status from article where id=?", id);
    return article.getStatus();
  }

  public Ret updateStatus(int id, int status) {
    Article article = dao.findById(id);
    if (article == null) return Ret.fail("msg", "文章不存在");
    if (BitKit.at(article.getStatus(), 1) == 0 && BitKit.at(status, 1) == 1) {
      article.setPublishAt(new Date().getTime());
    }
    article.setStatus(status);
    if (article.update()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }
}
