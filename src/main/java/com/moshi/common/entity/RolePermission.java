package com.moshi.common.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "role_permission", schema = "moshi2", catalog = "")
@IdClass(RolePermissionPK.class)
public class RolePermission {
  private int roleId;
  private int permissionId;

  private Role role;
  private Permission permission;

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Permission getPermission() {
    return permission;
  }

  public void setPermission(Permission permission) {
    this.permission = permission;
  }

  @Id
  @Column(name = "roleId")
  public int getRoleId() {
    return roleId;
  }

  public void setRoleId(int roleId) {
    this.roleId = roleId;
  }

  @Id
  @Column(name = "permissionId")
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
    RolePermission that = (RolePermission) o;
    return roleId == that.roleId &&
      permissionId == that.permissionId;
  }

  @Override
  public int hashCode() {
    return Objects.hash(roleId, permissionId);
  }
}
