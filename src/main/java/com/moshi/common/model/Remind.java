package com.moshi.common.model;

import io.jboot.Jboot;
import io.jboot.support.redis.JbootRedis;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Remind implements Serializable {
  public static final int TYPE_CREATE_ISSUE = 0;
  private static Lock lock = new ReentrantLock();

//  private Integer id;
  private Integer accountId;
  private Integer refId;
  private Integer type;
  private Long createAt;
  private Integer status;


  public Integer getAccountId() {
    return accountId;
  }

  public void setAccountId(Integer accountId) {
    this.accountId = accountId;
  }

  public Integer getRefId() {
    return refId;
  }

  public void setRefId(Integer refId) {
    this.refId = refId;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }

  public Long getCreateAt() {
    return createAt;
  }

  public void setCreateAt(Long createAt) {
    this.createAt = createAt;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

//  public Integer getId() {
//    return id;
//  }
}
