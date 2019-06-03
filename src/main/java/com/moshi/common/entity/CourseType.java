package com.moshi.common.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "course_type", schema = "moshi2", catalog = "")
public class CourseType {
  private int id;
  private String typeName;
  private int courseId;

  private Course course ;

  public Course getCourse() {
    return course;
  }

  public void setCourse(Course course) {
    this.course = course;
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
  @Column(name = "typeName")
  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  @Basic
  @Column(name = "courseId")
  public int getCourseId() {
    return courseId;
  }

  public void setCourseId(int courseId) {
    this.courseId = courseId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CourseType that = (CourseType) o;
    return id == that.id &&
      courseId == that.courseId &&
      Objects.equals(typeName, that.typeName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, typeName, courseId);
  }
}
