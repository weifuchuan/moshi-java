package com.moshi.common.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "paragraph_comment", schema = "moshi2", catalog = "")
public class ParagraphComment {
  private int id;
  private int paragraphId;
  private int accountId;
  private String content;
  private long createAt;
  private int status;
  private Integer replyTo;


  private Account account;
  private Paragraph paragraph;

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Paragraph getParagraph() {
    return paragraph;
  }

  public void setParagraph(Paragraph paragraph) {
    this.paragraph = paragraph;
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
  @Column(name = "paragraphId")
  public int getParagraphId() {
    return paragraphId;
  }

  public void setParagraphId(int paragraphId) {
    this.paragraphId = paragraphId;
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
  @Column(name = "replyTo")
  public Integer getReplyTo() {
    return replyTo;
  }

  public void setReplyTo(Integer replyTo) {
    this.replyTo = replyTo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ParagraphComment that = (ParagraphComment) o;
    return id == that.id &&
      paragraphId == that.paragraphId &&
      accountId == that.accountId &&
      createAt == that.createAt &&
      status == that.status &&
      Objects.equals(content, that.content) &&
      Objects.equals(replyTo, that.replyTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, paragraphId, accountId, content, createAt, status, replyTo);
  }
}
