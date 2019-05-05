package com.moshi.srv.v1.subscribe;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.redis.RedisPlugin;
import com.moshi.common.model.Course;
import com.moshi.common.model.Subscription;
import com.moshi.common.mq.RedisMQ;
import com.moshi.common.plugin.RedisMQPlugin;
import com.moshi.srv.v1.course.CourseService;
import com.moshi.subscription.SubscriptionService;

import java.util.Date;

public class SubscribeService {

  private Subscription dao = new Subscription().dao();
  private RedisMQ mq = RedisMQPlugin.getMq();

  public Ret generate(int payWay, int refId, String type, int accountId, double cost) {
    Subscription subscription = new Subscription();
    subscription.setId(Subscription.generateId());
    subscription.setPayWay(payWay);
    subscription.setAccountId(accountId);
    subscription.setCost(cost);
    subscription.setRefId(refId);
    subscription.setSubscribeType(type);
    subscription.setCreateAt(new Date().getTime());
    subscription.setStatus(Subscription.STATUS_INIT);
    if (subscription.save()) return Ret.ok("subscription", subscription);
    else return Ret.fail("msg", "订单生成失败");
  }

  public Ret confirm(String id, int accountId) {
    Subscription subscription = dao.findById(id);
    if (subscription == null) return Ret.fail("msg", "not exists");
    if (subscription.getStatus() != Subscription.STATUS_INIT) {
      return Ret.fail("msg", "status exception");
    }
    if (subscription.getAccountId() != accountId) {
      return Ret.fail("msg", "owner not matched");
    }
    subscription.setStatus(Subscription.STATUS_SUCCESS);
    if (subscription.getSubscribeType().equals(Subscription.SUB_TYPE_COURSE)) {
      Course course = Course.dao.findById(subscription.getRefId());
      if (course == null) {
        return Ret.fail("msg", "course not exists");
      }
      Db.update("update course set buyerCount = buyerCount + 1 where id = ?", course.getId());
    }
    if (subscription.update()) {
      SubscriptionService.me.clearCache(accountId);
      CourseService.Companion.getMe().clearHotCache(Course.TYPE_COLUMN);
      CourseService.Companion.getMe().clearHotCache(Course.TYPE_VIDEO);
      mq.publish("subscribed", subscription);
      return Ret.ok();
    } else {
      return Ret.fail("msg", "data update fail");
    }
  }

  public Ret cancel(String id, int accountId) {
    Subscription subscription = dao.findById(id);
    if (subscription == null) return Ret.fail("msg", "not exists");
    if (subscription.getStatus() != Subscription.STATUS_INIT) {
      return Ret.fail("msg", "status exception");
    }
    if (subscription.getAccountId() != accountId) {
      return Ret.fail("msg", "owner not matched");
    }
    subscription.setStatus(Subscription.STATUS_CANCEL);
    if (subscription.update()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "data update fail");
    }
  }

  public Ret fail(String id, int accountId) {
    Subscription subscription = dao.findById(id);
    if (subscription == null) return Ret.fail("msg", "not exists");
    if (subscription.getStatus() != Subscription.STATUS_INIT) {
      return Ret.fail("msg", "status exception");
    }
    if (subscription.getAccountId() != accountId) {
      return Ret.fail("msg", "owner not matched");
    }
    subscription.setStatus(Subscription.STATUS_FAIL);
    if (subscription.update()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "data update fail");
    }
  }

  public Ret delete(String id, int accountId) {
    Subscription subscription = dao.findById(id);
    if (subscription == null) return Ret.fail("msg", "not exists");
    if (subscription.getAccountId() != accountId) {
      return Ret.fail("msg", "owner not matched");
    }
    if (subscription.delete()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "data delete fail");
    }
  }
}
