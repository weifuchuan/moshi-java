package com.moshi.issue;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Record;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.IssueComment;


import java.util.List;


public class IssueController extends BaseController {
  @Inject
  private IssueService srv;

  public void fetch(){
    int courseId = getParaToInt("courseId");
    List<Record> records = srv.find(courseId);
    renderJson(records);
  }

  public void comment(){
    int issueId=getParaToInt("issueId");
    String content=getPara("content");
    renderJson(srv.comment(issueId, content, getLoginAccountId()));
  }
}
