package com.moshi.srv.v1.article;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.ehcache.CacheKit;
import com.moshi.common.kit.BitKit;
import com.moshi.common.model.Account;
import com.moshi.common.model.Article;
import com.moshi.common.model.ArticleComment;
import com.moshi.common.model.Audio;
import com.moshi.common.plugin.Letture;
import com.moshi.srv.v1.statistics.StatisticsService;
import com.moshi.subscription.SubscriptionService;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ArticleService {
  public static final String CACHE_NAME = "article";

  private Article dao = new Article().dao();
  private SubscriptionService subSrv = new SubscriptionService();
  private StatisticsService statisticsService = new StatisticsService();
  private ArticleComment commentDao = new ArticleComment().dao();
  private Audio audioDao = new Audio().dao();

  public List<Article> trialReading(int courseId) {
    SqlPara sqlPara = dao.getSqlPara("article.trialReading", courseId);
    List<Article> articles =
        new ArrayList<>(
            dao.findByCache(
                CACHE_NAME, "trialReading:" + courseId, sqlPara.getSql(), sqlPara.getPara()));
    return articles;
  }

  public Page<Article> list(
      int courseId, String cending, int pageNumber, int pageSize, Integer accountId) {
    Page<Article> paginate =
        dao.paginate(
            pageNumber,
            pageSize,
            dao.getSqlPara("article.list", Kv.by("cending", cending).set("courseId", courseId)));
    if (accountId != null) {
      paginate
          .getList()
          .forEach(
              a -> {
                long visitAt = statisticsService.lastVisitAt(accountId, a.getId(), "article");
                a.put("visitAt", visitAt);
                a.put("readed", visitAt != 0);
              });
    }
    return paginate;
  }

  public Ret findById(int id, Integer accountId) {
    Article article = dao.findById(id);
    if (article == null) return Ret.fail("msg", "文章不存在");
    if (BitKit.at(article.getStatus(), 0) == 1) {
      return Ret.fail("msg", "文章已锁定");
    }
    if (BitKit.at(article.getStatus(), 1) == 0) {
      return Ret.fail("msg", "文章未发布");
    }
    if (BitKit.at(article.getStatus(), 2) == 0) {
      boolean subscribed =
          accountId != null && subSrv.subscribedCourse(article.getCourseId(), accountId);
      if (!subscribed) {
        return Ret.fail("msg", "未订阅专栏");
      }
    }
    Record author =
        Db.findFirst(
            " select a.nickName "
                + " from account a, course c, article t "
                + " where t.id = ? and c.id = t.courseId and c.accountId = a.id",
            article.getId());
    article.put(author);
    List<ArticleComment> comments =
        ArticleComment.dao.find(Db.getSqlPara("article.findComments", id));
    Ret ret = Ret.ok("article", article).set("comments", comments);
    if (accountId != null) {
      article.put("liked", liked(id, accountId));
      comments.forEach(
          c -> {
            c.put("liked", c.liked(accountId));
          });
    }
    article.put("likeCount", likeCount(id));
    if (article.getAudioId() != null) {
      Audio audio = audioDao.findById(article.getAudioId());
      if (audio != null) {
        article.put(
            "audio", Kv.by("recorder", audio.getRecorder()).set("resource", audio.getResource()));
      }
    }
    return ret;
  }

  public Ret comment(int articleId, String content, int accountId) {
    ArticleComment comment = new ArticleComment();
    comment.setAccountId(accountId);
    comment.setArticleId(articleId);
    comment.setContent(content);
    comment.setCreateAt(new Date().getTime());
    if (comment.save()) {
      return Ret.ok("id", comment.getId()).set("createAt", comment.getCreateAt());
    } else {
      return Ret.fail("msg", "评论保存失败");
    }
  }

  public Ret like(int id, int accountId) {
    Letture.async().sadd("like:article:" + id, accountId);
    return Ret.ok();
  }

  public boolean liked(int id, int accountId) {
    return Letture.sync().sismember("like:article:" + id, accountId);
  }

  public long likeCount(int id) {
    Long scard = Letture.sync().scard("like:article:" + id);
    return scard == null ? 0 : scard;
  }

  public Ret unlike(int id, int accountId) {
    Letture.async().srem("like:article:" + id, accountId);
    return Ret.ok();
  }

  public void clearTrialReadingCache(int courseId) {
    CacheKit.remove(CACHE_NAME, "trialReading:" + courseId);
  }
}
