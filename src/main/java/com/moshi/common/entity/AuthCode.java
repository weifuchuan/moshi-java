package com.moshi.common.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "auth_code", schema = "moshi2", catalog = "")
public class AuthCode {
  private String id;
  private int accountId;
  private long expireAt;
  private int type;

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
  @Column(name = "expireAt")
  public long getExpireAt() {
    return expireAt;
  }

  public void setExpireAt(long expireAt) {
    this.expireAt = expireAt;
  }

  @Basic
  @Column(name = "type")
  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthCode authCode = (AuthCode) o;
    return accountId == authCode.accountId &&
      expireAt == authCode.expireAt &&
      type == authCode.type &&
      Objects.equals(id, authCode.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accountId, expireAt, type);
  }
}
