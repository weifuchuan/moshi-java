package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Application {
  private int id;
  private int accountId;
  private int category;
  private String title;
  private String content;
  private long createAt;
  private int status;
  private String reply;
  private int refId;
  private String contentType;
  private Account account;

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
  @Column(name = "accountId")
  public int getAccountId() {
    return accountId;
  }

  public void setAccountId(int accountId) {
    this.accountId = accountId;
  }

  @Basic
  @Column(name = "category")
  public int getCategory() {
    return category;
  }

  public void setCategory(int category) {
    this.category = category;
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
  @Column(name = "content")
  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
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

  @Basic
  @Column(name = "reply")
  public String getReply() {
    return reply;
  }

  public void setReply(String reply) {
    this.reply = reply;
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
  @Column(name = "contentType")
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Application that = (Application) o;
    return id == that.id &&
      accountId == that.accountId &&
      category == that.category &&
      createAt == that.createAt &&
      status == that.status &&
      refId == that.refId &&
      Objects.equals(title, that.title) &&
      Objects.equals(content, that.content) &&
      Objects.equals(reply, that.reply) &&
      Objects.equals(contentType, that.contentType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accountId, category, title, content, createAt, status, reply, refId, contentType);
  }
}
