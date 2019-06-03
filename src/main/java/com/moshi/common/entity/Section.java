package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Section {
  private int id;
  private int courseId;
  private int accountId;
  private String title;
  private long createAt;
  private int status;

  private Course course;
  private Account account;

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  @Id
  @Column(name = "id")
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Basic
  @Column(name = "courseId")
  public int getCourseId() {
    return courseId;
  }

  public void setCourseId(int courseId) {
    this.courseId = courseId;
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
  @Column(name = "title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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
    Section section = (Section) o;
    return id == section.id &&
      courseId == section.courseId &&
      accountId == section.accountId &&
      createAt == section.createAt &&
      status == section.status &&
      Objects.equals(title, section.title);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, courseId, accountId, title, createAt, status);
  }
}
