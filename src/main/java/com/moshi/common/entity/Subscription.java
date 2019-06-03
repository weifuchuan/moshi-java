package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Subscription {
  private String id;
  private int accountId;
  private int refId;
  private String subscribeType;
  private long createAt;
  private double cost;
  private int status;
  private int payWay;

  private Account account;

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  @Id
  @Column(name = "id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Basic
  @Column(name = "accountId")
  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  @Basic
  @Column(name = "refId")
  public int getRefId() {
    return refId;
  }

  public void setRefId(int refId) {
    this.refId = refId;
  }

  @Basic
  @Column(name = "subscribeType")
  public String getSubscribeType() {
    return subscribeType;
  }

  public void setSubscribeType(String subscribeType) {
    this.subscribeType = subscribeType;
  }

  @Basic
  @Column(name = "createAt")
  public long getCreateAt() {
    return createAt;
  }

  public void setCreateAt(long createAt) {
    this.createAt = createAt;
  }

  @Basic
  @Column(name = "cost")
  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  @Basic
  @Column(name = "status")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Basic
  @Column(name = "payWay")
  public int getPayWay() {
    return payWay;
  }

  public void setPayWay(int payWay) {
    this.payWay = payWay;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Subscription that = (Subscription) o;
    return accountId == that.accountId &&
      refId == that.refId &&
      createAt == that.createAt &&
      Double.compare(that.cost, cost) == 0 &&
      status == that.status &&
      payWay == that.payWay &&
      Objects.equals(id, that.id) &&
      Objects.equals(subscribeType, that.subscribeType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accountId, refId, subscribeType, createAt, cost, status, payWay);
  }
}
