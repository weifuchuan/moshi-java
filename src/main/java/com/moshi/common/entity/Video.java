package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Video {
  private int id;
  private String resource;
  private String recorder;
  private int accountId;
  private int status;
  private String name;
  private long uploadAt;

  @Id
  @Column(name = "id")
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Basic
  @Column(name = "resource")
  public String getResource() {
    return resource;
  }

  public void setResource(String resource) {
    this.resource = resource;
  }

  @Basic
  @Column(name = "recorder")
  public String getRecorder() {
    return recorder;
  }

  public void setRecorder(String recorder) {
    this.recorder = recorder;
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
  @Column(name = "status")
  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  @Basic
  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Basic
  @Column(name = "uploadAt")
  public long getUploadAt() {
    return uploadAt;
  }

  public void setUploadAt(long uploadAt) {
    this.uploadAt = uploadAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Video video = (Video) o;
    return id == video.id &&
      accountId == video.accountId &&
      status == video.status &&
      uploadAt == video.uploadAt &&
      Objects.equals(resource, video.resource) &&
      Objects.equals(recorder, video.recorder) &&
      Objects.equals(name, video.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, resource, recorder, accountId, status, name, uploadAt);
  }
}
