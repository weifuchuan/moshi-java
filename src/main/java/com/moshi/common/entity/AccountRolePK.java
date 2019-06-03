package com.moshi.common.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class AccountRolePK implements Serializable {
  private int accountId;
  private int roleId;

  @Column(name = "accountId")
  @Id
  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  @Column(name = "roleId")
  @Id
  public int getRoleId() {
    return roleId;
  }

  public void setRoleId(int roleId) {
    this.roleId = roleId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AccountRolePK that = (AccountRolePK) o;
    return accountId == that.accountId &&
      roleId == that.roleId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, roleId);
  }
}
