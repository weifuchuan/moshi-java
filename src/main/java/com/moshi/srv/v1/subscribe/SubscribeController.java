package com.moshi.srv.v1.subscribe;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.AesKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.render.QrCodeRender;
import com.moshi.common.MainConfig;
import com.moshi.common.controller.BaseController;
import com.moshi.common.interceptor.UnlockInterceptor;
import com.moshi.common.kit.ConfigKit;
import com.moshi.common.model.Coupon;
import com.moshi.common.model.Course;
import com.moshi.common.model.Subscription;
import com.moshi.common.plugin.Letture;
import com.moshi.srv.v1.coupon.CouponService;
import com.moshi.srv.v1.course.CourseService;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SubscribeController extends BaseController {
  private static final Set<String> typeSet = Stream.of("course").collect(Collectors.toSet());

  static final PublishSubject<Kv> SUBJECT = PublishSubject.create();

  // 订单过期时间：一小时
  private static final int SUBSCRIPTION_TIMEOUT = 60;

  private static final String aesKey = AesKit.genAesKey();

  @Inject SubscribeService srv;
  @Inject CouponService couponSrv;

  @Before(UnlockInterceptor.class)
  public void index(int payWay, int refId, String type, String[] coupons) {
    coupons = coupons == null ? new String[0] : coupons;
    if (!typeSet.contains(type)) renderJson(Ret.fail("msg", "Unallowed type"));
    switch (type) {
      case "course":
        Course course = Course.dao.findById(refId);
        List<Coupon> couponList =
            Stream.of(coupons)
                .map(id -> couponSrv.verify(id, getLoginAccountId()))
                .filter(ret -> ret.isOk())
                .map(ret -> (Coupon) ret.get("coupon"))
                .collect(Collectors.toList());
        double price = course.getPrice();
        if (course.getOfferTo() != null
            && course.getOfferTo() > new Date().getTime()
            && course.getDiscountedPrice() != null) {
          price = course.getDiscountedPrice();
        }
        if (couponList.size() > 0) {
          Coupon coupon = couponList.get(0);
          if (coupon.getValueType() == Coupon.VALUE_TYPE_MONEY) {
            price -= coupon.getValue();
            price = price >= 0 ? price : 0;
          } else if (coupon.getValueType() == Coupon.VALUE_TYPE_DISCOUNT) {
            price *= coupon.getValue();
            price = price >= 0 ? price : 0;
          }
        }
        Ret ret = srv.generate(payWay, refId, type, getLoginAccountId(), price);
        if (ret.isOk()) {
          Subscription subscription = (Subscription) ret.get("subscription");
          String id = subscription.getId();
          Observable.timer(SUBSCRIPTION_TIMEOUT, TimeUnit.MINUTES)
              .subscribe(
                  (i) -> {
                    Subscription subscription1 = Subscription.dao.findById(id);
                    if (subscription1.getStatus() == Subscription.STATUS_INIT) {
                      subscription1.delete();
                    }
                  });
          ret.set("timeout", SUBSCRIPTION_TIMEOUT);
        }
        renderJson(ret);
        break;
      default:
        renderJson(Ret.fail());
    }
  }

  @Before(UnlockInterceptor.class)
  public void qr(String id) {
    if (id == null) {
      renderError(404);
      return;
    }
    String params = JSON.toJSONString(Kv.by("id", id).set("accountId", getLoginAccountId()));
    params = HexUtil.encodeHexStr(AesKit.encrypt(params, aesKey));
    renderQrCode(ConfigKit.HOST_PORT + "/srv/v1/subscribe/callback?params=" + params, 200, 200);
    System.out.println(
        "\n\n\t" + ConfigKit.HOST_PORT + "/srv/v1/subscribe/callback?params=" + params);
  }

  @Before(UnlockInterceptor.class)
  public void confirm(String id) {
    Subscription subscription = Subscription.dao.findById(id);
    if (subscription != null && subscription.getAccountId() == getLoginAccountId()) {
      String key = AesKit.genAesKey();
      Letture.async().setex("subscribe:" + key, 60 * 60, id);
      renderJson(
          Kv.by("key", key)
              .set(
                  "url",
                  MainConfig.Companion.getP().get("host").trim()
                      + ":"
                      + MainConfig.Companion.getP().get("socketio.port").trim()
                      + "/srv/v1/subscribe/confirm"));
    } else {
      renderError(404);
    }
  }

  public void callback(String params) {
    String json = AesKit.decryptToStr(HexUtil.decodeHex(params), aesKey);
    JSONObject jsonObject = JSON.parseObject(json);
    String id = jsonObject.getString("id");
    int accountId = jsonObject.getInteger("accountId");
    Ret ret = srv.confirm(id, accountId);
    SUBJECT.onNext(Kv.by("id", id).set("ret", ret));
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void cancel(String id) {
    Ret ret = srv.cancel(id, getLoginAccountId());
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void fail(String id) {
    Ret ret = srv.fail(id, getLoginAccountId());
    renderJson(ret);
  }

  @Before(UnlockInterceptor.class)
  public void delete(String id) {
    Ret ret = srv.delete(id, getLoginAccountId());
    renderJson(ret);
  }
}
