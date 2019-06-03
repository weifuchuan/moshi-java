package com.moshi.common.entity;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class RolePermissionPK implements Serializable {
  private int roleId;
  private int permissionId;

  @Column(name = "roleId")
  @Id
  public int getRoleId() {
    return roleId;
  }

  public void setRoleId(int roleId) {
    this.roleId = roleId;
  }

  @Column(name = "permissionId")
  @Id
  public int getPermissionId() {
    return permissionId;
  }

  public void setPermissionId(int permissionId) {
    this.permissionId = permissionId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RolePermissionPK that = (RolePermissionPK) o;
    return roleId == that.roleId &&
      permissionId == that.permissionId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(roleId, permissionId);
  }
}
