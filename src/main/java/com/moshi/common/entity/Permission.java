package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Permission {
  private int id;
  private String actionKey;
  private String controller;
  private String remark;

  @Id
  @Column(name = "id")
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Basic
  @Column(name = "actionKey")
  public String getActionKey() {
    return actionKey;
  }

  public void setActionKey(String actionKey) {
    this.actionKey = actionKey;
  }

  @Basic
  @Column(name = "controller")
  public String getController() {
    return controller;
  }

  public void setController(String controller) {
    this.controller = controller;
  }

  @Basic
  @Column(name = "remark")
  public String getRemark() {
    return remark;
  }

  public void setRemark(String remark) {
    this.remark = remark;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Permission that = (Permission) o;
    return id == that.id &&
      Objects.equals(actionKey, that.actionKey) &&
      Objects.equals(controller, that.controller) &&
      Objects.equals(remark, that.remark);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, actionKey, controller, remark);
  }
}
