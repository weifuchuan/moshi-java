package com.moshi.srv.v1.statistics;

import com.jfinal.kit.Ret;
import com.moshi.common.plugin.Letture;
import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.*;

public class StatisticsService {

  private static final Set<String> filterVisitTypeSet = new HashSet<String>();

  static {
    filterVisitTypeSet.add("article");
    filterVisitTypeSet.add("course");
    filterVisitTypeSet.add("video");
    filterVisitTypeSet.add("news");
    filterVisitTypeSet.add("account");
    filterVisitTypeSet.add("issue");
    filterVisitTypeSet.add("paragraph");
  }

  /**
   * @param accountId
   * @param refId
   * @param type article, course, video, news, account, issue, paragraph
   */
  public Ret visit(Integer accountId, int refId, String type) {
    if (accountId == null) return visit(refId, type);
    if (!filterVisitTypeSet.contains(type)) {
      return Ret.fail("msg", "Unsupport type: " + type);
    }
    RedisAsyncCommands<String, Object> async = Letture.async();
    async.multi();
    async.zadd("visit:" + type, 1, refId);
    async.sadd("visit:" + type + ":" + refId, accountId);
    async.exec();
    if (type.equals("article")) {
      lastVisit(accountId, refId, type);
    }
    return Ret.ok();
  }

  public Ret visit(int refId, String type) {
    if (!filterVisitTypeSet.contains(type)) {
      return Ret.fail("msg", "Unsupport type: " + type);
    }
    Letture.async().zadd("visit:" + type, 1, refId);
    return Ret.ok();
  }

  public int visitCount(int refId, String type) {
    Double zscore = Letture.sync().zscore("visit:" + type, refId);
    if (zscore == null) return 0;
    return (int) (double) zscore;
  }

  public Ret lastVisit(int accountId, int refId, String type) {
    if (!filterVisitTypeSet.contains(type)) {
      return Ret.fail("msg", "Unsupport type: " + type);
    }
    Letture.async().hset("lastVisit:" + accountId, type + ":" + refId, new Date().getTime());
    return Ret.ok();
  }

  public long lastVisitAt(int accountId, int refId, String type) {
    Object at = Letture.sync().hget("lastVisit:" + accountId, type + ":" + refId);
    if (at == null) return 0;
    return (Long) at;
  }
}
