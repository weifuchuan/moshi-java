package com.moshi.subscription;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;
import com.moshi.common.model.Subscription;


import java.util.ArrayList;
import java.util.List;

public class SubscriptionService {
  public static final String cacheName = "subscription";

  public static final SubscriptionService me = new SubscriptionService();

  private Subscription dao = new Subscription().dao();

  public List<Subscription> findByAccountId(int accountId) {
    List<Subscription> subscriptionList =
        new ArrayList<>(
            dao.findByCache(
                SubscriptionService.cacheName,
                "" + accountId,
                "select * from subscription where accountId = ?",
                accountId));
    return subscriptionList;
  }

  public boolean subscribedCourse(int refId, int accountId) {
    return subscribed(refId, Subscription.SUB_TYPE_COURSE, accountId);
  }

  public boolean subscribed(int refId, String type, int accountId) {
    if (CacheKit.get("subscribed", type + ":" + refId + ":" + accountId) != null) {
      return true;
    }
    Record first =
        Db.findFirst(
            " select id from subscription "
                + " where refId = ? and subscribeType = ? and accountId = ? and status = ? "
                + " limit 1 ",
            refId,
            type,
            accountId,
            Subscription.STATUS_SUCCESS);
    if (first != null) {
      CacheKit.put("subscribed", type + ":" + refId + ":" + accountId, true);
    }
    return first != null;
  }

  public void clearCache(int accountId) {
    CacheKit.remove(SubscriptionService.cacheName, "" + accountId);
  }
}
