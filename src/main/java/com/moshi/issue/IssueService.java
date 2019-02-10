package com.moshi.issue;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.moshi.common.model.Issue;
import com.moshi.common.model.IssueComment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IssueService {
  public static final IssueService me = new IssueService();
  private Issue dao = Issue.dao;

  public List<Record> find(int courseId) {
    List<Record> issues = Db.find(Db.getSqlPara("issue.find", courseId));
    return issues;
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
}
