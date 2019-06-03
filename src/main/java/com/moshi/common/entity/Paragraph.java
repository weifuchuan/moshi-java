package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Paragraph {
  private int id;
  private int sectionId;
  private int accountId;
  private int vedioId;
  private String content;
  private long createAt;
  private int status;

  private Account account;
  private Section section;
  private Video video;

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public Section getSection() {
    return section;
  }

  public void setSection(Section section) {
    this.section = section;
  }

  public Video getVideo() {
    return video;
  }

  public void setVideo(Video video) {
    this.video = video;
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
  @Column(name = "sectionId")
  public int getSectionId() {
    return sectionId;
  }

  public void setSectionId(int sectionId) {
    this.sectionId = sectionId;
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
  @Column(name = "vedioId")
  public int getVedioId() {
    return vedioId;
  }

  public void setVedioId(int vedioId) {
    this.vedioId = vedioId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Paragraph paragraph = (Paragraph) o;
    return id == paragraph.id &&
      sectionId == paragraph.sectionId &&
      accountId == paragraph.accountId &&
      vedioId == paragraph.vedioId &&
      createAt == paragraph.createAt &&
      status == paragraph.status &&
      Objects.equals(content, paragraph.content);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sectionId, accountId, vedioId, content, createAt, status);
  }
}
