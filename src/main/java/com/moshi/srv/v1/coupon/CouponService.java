package com.moshi.srv.v1.coupon;

import com.jfinal.kit.Ret;
import com.moshi.common.model.Coupon;

import java.util.Date;

public class CouponService {
  private Coupon dao = new Coupon().dao();

  public Ret gen(double value, int valueType, int accountId, Integer refId, int refType, int days) {
    Coupon coupon = new Coupon();
    coupon.setId(Coupon.generateId());
    coupon.setValue(value);
    coupon.setValueType(valueType);
    coupon.setAccountId(accountId);
    coupon.setRefId(refId);
    coupon.setRefType(refType);
    long now = new Date().getTime();
    coupon.setCreateAt(now);
    coupon.setOfferTo(now + days * 1000 * 60 * 60 * 24);
    coupon.setStatus(Coupon.STATUS_USED);
    if (coupon.save()) {
      return Ret.ok("coupon", coupon);
    }
    return Ret.fail("msg", "生成失败");
  }

  public Ret verify(String id, int accountId) {
    Coupon coupon = dao.findById(id);
    if (coupon == null) {
      return Ret.fail("msg", "优惠券不存在");
    }
    if (coupon.getStatus() != Coupon.STATUS_USED) {
      if (coupon.getStatus() == Coupon.STATUS_TIMEOUT) return Ret.fail("msg", "优惠券已过期");
      else return Ret.fail("msg", "优惠券已失效");
    }
    if (coupon.getOfferTo() <= new Date().getTime()) {
      coupon.setStatus(Coupon.STATUS_TIMEOUT);
      coupon.update();
      return Ret.fail("msg", "优惠券已过期");
    }
    if (coupon.getAccountId() != accountId) {
      return Ret.fail("msg", "优惠券不属于此用户");
    }
    return Ret.ok("coupon", coupon);
  }
}
