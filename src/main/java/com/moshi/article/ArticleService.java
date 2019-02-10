package com.moshi.article;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.moshi.common.model.Article;
import com.moshi.common.model.ArticleComment;
import com.moshi.common.model.Course;

import java.util.Date;
import java.util.List;

public class ArticleService {
  public static final ArticleService me = new ArticleService();

  private Article dao = new Article().dao();

  public Ret findAllUsefulData(int id) {
    Article article = dao.findById(id);
    if (article == null) {
      return Ret.fail("msg", "Article not exists. ");
    }
    List<ArticleComment> comments =
        ArticleComment.dao.find(Db.getSqlPara("article.findComments", id));
    return Ret.ok("article", article).set("comments", comments);
  }

  public Ret create(String title, String content, int courseId, int audioId) {
    Course first = Course.dao.findFirst("select id from course where id = ? ", courseId);
    if (first == null) {
      return Ret.fail("msg", "课程不存在");
    }
    Article article = new Article();
    article.setTitle(title);
    article.setContent(content);
    article.setCourseId(courseId);
    article.setCreateAt(new Date().getTime());
    article.setAudioId(audioId);
    if (article.save()) {
      return Ret.ok("id", article.getId())
          .set("createAt", article.getCreateAt())
          .set("status", article.getStatus());
    } else {
      return Ret.fail("msg", "文章保存失败");
    }
  }

  public Ret update(int id, Kv items) {
    int updated = Db.update(Db.getSqlPara("article.update", Kv.by("items", items).set("id", id)));
    if (updated > 0) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }

  public Ret comment(int articleId, String content, Integer replyTo, int accountId) {
    ArticleComment comment = new ArticleComment();
    comment.setAccountId(accountId);
    comment.setArticleId(articleId);
    comment.setContent(content);
    comment.setCreateAt(new Date().getTime());
    if (replyTo != null) comment.setReplyTo(replyTo);
    if (comment.save()) {
      return Ret.ok("id", comment.getId()).set("createAt", comment.getCreateAt());
    } else {
      return Ret.fail("msg", "评论保存失败");
    }
  }

  public Ret deleteComment(int articleId, int commentId, int accountId) {
    SqlPara sqlPara =
        Db.getSqlPara(
            "article.deleteComment",
            Kv.by("articleId", articleId).set("commentId", commentId).set("accountId", accountId));
    if (Db.delete(sqlPara.getSql(), sqlPara.getPara()) > 0) {
      // TODO: delete comments which replyTo this comment
      return Ret.ok();
    } else {
      return Ret.fail("msg", "删除失败：没有权限");
    }
  }

  public Ret updateCommentStatus(int commentId, int status, int articleId, int accountId) {
    if (Db.update(
            Db.getSqlPara(
                "article.updateCommentStatus",
                Kv.by("commentId", commentId)
                    .set("status", status)
                    .set("articleId", articleId)
                    .set("accountId", accountId)))
        > 0) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }
}
