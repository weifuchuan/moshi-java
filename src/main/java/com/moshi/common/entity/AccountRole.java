package com.moshi.common.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "account_role", schema = "moshi2", catalog = "")
@IdClass(AccountRolePK.class)
public class AccountRole {
  private int accountId;
  private int roleId;
  private Account account;
  private Role role;

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  @Id
  @Column(name = "accountId")
  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  @Id
  @Column(name = "roleId")
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
    AccountRole that = (AccountRole) o;
    return accountId == that.accountId &&
      roleId == that.roleId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(accountId, roleId);
  }
}
