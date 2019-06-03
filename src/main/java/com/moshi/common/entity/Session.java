package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Session {
  private String id;
  private int accountId;
  private long expireAt;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Session session = (Session) o;
    return accountId == session.accountId &&
      expireAt == session.expireAt &&
      Objects.equals(id, session.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accountId, expireAt);
  }
}
