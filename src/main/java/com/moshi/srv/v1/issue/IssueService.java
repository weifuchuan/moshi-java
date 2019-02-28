package com.moshi.srv.v1.issue;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.moshi.common.model.Issue;
import com.moshi.common.model.IssueComment;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;

import java.util.Date;
import java.util.List;

public class IssueService {
  Issue dao = new Issue().dao();
  IssueComment icDao = new IssueComment().dao();
  JbootRedis redis = Jboot.getRedis();

  public Ret findById(int id) {
    Issue issue = dao.findFirst(dao.getSqlPara("issue.findById", id));
    if (issue == null) return Ret.fail("msg", "issue not exists");
    List<IssueComment> comments = icDao.find(icDao.getSqlPara("issue.findComments", id));
    return Ret.ok("issue", issue).set("comments", comments);
  }

  public Ret list(int courseId, String filter, int pageNumber, int pageSize) {
    Page<Issue> page =
        dao.paginate(
            pageNumber,
            pageSize,
            dao.getSqlPara("issue.list", Kv.by("courseId", courseId).set("filter", filter)));
    return Ret.ok("page", page);
  }

  public Ret create(int courseId, int accountId, String title, String content) {
    Issue issue = new Issue();
    issue.setCourseId(courseId);
    issue.setAccountId(accountId);
    issue.setTitle(title);
    issue.setOpenAt(new Date().getTime());
    issue.setStatus(Issue.STATUS_OPEN);
    if (issue.save()) {
      IssueComment first = new IssueComment();
      first.setIssueId(issue.getId());
      first.setAccountId(accountId);
      first.setCreateAt(issue.getOpenAt());
      first.setContent(content);
      first.save();
      return Ret.ok("id", issue.getId())
          .set("openAt", issue.getOpenAt())
          .set("commentId", first.getId());
    } else {
      return Ret.fail("msg", "创建失败");
    }
  }

  public Ret close(int id, int accountId, String reason) {
    if (Db.findFirst("select status where id=? and status=?", id, Issue.STATUS_CLOSE) != null) {
      return Ret.fail("msg", "Issue已被关闭");
    }
    long now = new Date().getTime();
    int update =
        Db.update(
            "update issue set status = ?, closeAt = ? where id = ?",
            Issue.STATUS_CLOSE,
            accountId,
            now,
            id);
    if (update > 0) {
      redis.lpush(
          "issue:close:" + id + ":reason",
          Kv.by("reason", reason).set("closeAt", now).set("accountId", accountId));
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }

  public Ret open(int id, int accountId, String reason) {
    if (Db.findFirst("select status where id=? and status=?", id, Issue.STATUS_CLOSE) == null) {
      return Ret.fail("msg", "Issue未被关闭");
    }
    long now = new Date().getTime();
    int update =
        Db.update(
            "update issue set status = ?, openAt = ? where id = ?", Issue.STATUS_OPEN, now, id);
    if (update > 0) {
      redis.lpush(
          "issue:open:" + id + ":reason",
          Kv.by("reason", reason).set("openAt", now).set("accountId", accountId));
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }

  public void listComment(int issueId, int pageNumber, int pageSize) {
    // TODO

  }


  public Ret comment(int issueId, String content, int accountId) {
    IssueComment comment = new IssueComment();
    comment.setAccountId(accountId);
    comment.setContent(content);
    comment.setCreateAt(new Date().getTime());
    comment.setIssueId(issueId);
    if (comment.save()) {
      return Ret.ok("id", comment.getId())
        .set("createAt", comment.getCreateAt()) ;
    } else {
      return Ret.fail("msg", "评论失败");
    }
  }

  public void updateComment(int id, String content) {
    // TODO
  }

  public void deleteComment(int id) {
    // TODO
  }
}
