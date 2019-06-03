package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Coupon {
  private String id;
  private double value;
  private int valueType;
  private int accountId;
  private Integer refId;
  private int refType;
  private long createAt;
  private long offerTo;
  private int status;

  @Id
  @Column(name = "id")
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Basic
  @Column(name = "value")
  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }

  @Basic
  @Column(name = "valueType")
  public int getValueType() {
    return valueType;
  }

  public void setValueType(int valueType) {
    this.valueType = valueType;
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
  public Integer getRefId() {
    return refId;
  }

  public void setRefId(Integer refId) {
    this.refId = refId;
  }

  @Basic
  @Column(name = "refType")
  public int getRefType() {
    return refType;
  }

  public void setRefType(int refType) {
    this.refType = refType;
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
  @Column(name = "offerTo")
  public long getOfferTo() {
    return offerTo;
  }

  public void setOfferTo(long offerTo) {
    this.offerTo = offerTo;
  }

  @Basic
  @Column(name = "status")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Coupon coupon = (Coupon) o;
    return Double.compare(coupon.value, value) == 0 &&
      valueType == coupon.valueType &&
      accountId == coupon.accountId &&
      refType == coupon.refType &&
      createAt == coupon.createAt &&
      offerTo == coupon.offerTo &&
      status == coupon.status &&
      Objects.equals(id, coupon.id) &&
      Objects.equals(refId, coupon.refId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, value, valueType, accountId, refId, refType, createAt, offerTo, status);
  }
}
