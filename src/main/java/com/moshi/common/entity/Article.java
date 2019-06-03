package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Article {
  private int id;
  private int courseId;
  private String title;
  private String content;
  private Long publishAt;
  private long createAt;
  private int status;
  private int audioId;
  private String contentType;
  private String summary;
  private String coverImage;

  private Course course ;
  private Audio audio ;

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
  }

  public Audio getAudio() {
    return audio;
  }

  public void setAudio(Audio audio) {
    this.audio = audio;
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
  @Column(name = "publishAt")
  public Long getPublishAt() {
    return publishAt;
  }

  public void setPublishAt(Long publishAt) {
    this.publishAt = publishAt;
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
  @Column(name = "audioId")
  public int getAudioId() {
    return audioId;
  }

  public void setAudioId(int audioId) {
    this.audioId = audioId;
  }

  @Basic
  @Column(name = "contentType")
  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Basic
  @Column(name = "summary")
  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  @Basic
  @Column(name = "coverImage")
  public String getCoverImage() {
    return coverImage;
  }

  public void setCoverImage(String coverImage) {
    this.coverImage = coverImage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Article article = (Article) o;
    return id == article.id &&
      courseId == article.courseId &&
      createAt == article.createAt &&
      status == article.status &&
      audioId == article.audioId &&
      Objects.equals(title, article.title) &&
      Objects.equals(content, article.content) &&
      Objects.equals(publishAt, article.publishAt) &&
      Objects.equals(contentType, article.contentType) &&
      Objects.equals(summary, article.summary) &&
      Objects.equals(coverImage, article.coverImage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, courseId, title, content, publishAt, createAt, status, audioId, contentType, summary, coverImage);
  }
}
