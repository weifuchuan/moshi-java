package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Course {
  private int id;
  private int accountId;
  private String name;
  private String introduce;
  private String shortIntro;
  private String introduceImage;
  private String note;
  private long createAt;
  private Long publishAt;
  private int buyerCount;
  private int courseType;
  private Double price;
  private Double discountedPrice;
  private Long offerTo;
  private int status;
  private int lectureCount;

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
  @Column(name = "name")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Basic
  @Column(name = "introduce")
  public String getIntroduce() {
    return introduce;
  }

  public void setIntroduce(String introduce) {
    this.introduce = introduce;
  }

  @Basic
  @Column(name = "shortIntro")
  public String getShortIntro() {
    return shortIntro;
  }

  public void setShortIntro(String shortIntro) {
    this.shortIntro = shortIntro;
  }

  @Basic
  @Column(name = "introduceImage")
  public String getIntroduceImage() {
    return introduceImage;
  }

  public void setIntroduceImage(String introduceImage) {
    this.introduceImage = introduceImage;
  }

  @Basic
  @Column(name = "note")
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
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
  @Column(name = "publishAt")
  public Long getPublishAt() {
    return publishAt;
  }

  public void setPublishAt(Long publishAt) {
    this.publishAt = publishAt;
  }

  @Basic
  @Column(name = "buyerCount")
  public int getBuyerCount() {
    return buyerCount;
  }

  public void setBuyerCount(int buyerCount) {
    this.buyerCount = buyerCount;
  }

  @Basic
  @Column(name = "courseType")
  public int getCourseType() {
    return courseType;
  }

  public void setCourseType(int courseType) {
    this.courseType = courseType;
  }

  @Basic
  @Column(name = "price")
  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  @Basic
  @Column(name = "discountedPrice")
  public Double getDiscountedPrice() {
    return discountedPrice;
  }

  public void setDiscountedPrice(Double discountedPrice) {
    this.discountedPrice = discountedPrice;
  }

  @Basic
  @Column(name = "offerTo")
  public Long getOfferTo() {
    return offerTo;
  }

  public void setOfferTo(Long offerTo) {
    this.offerTo = offerTo;
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
  @Column(name = "lectureCount")
  public int getLectureCount() {
    return lectureCount;
  }

  public void setLectureCount(int lectureCount) {
    this.lectureCount = lectureCount;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Course course = (Course) o;
    return id == course.id &&
      accountId == course.accountId &&
      createAt == course.createAt &&
      buyerCount == course.buyerCount &&
      courseType == course.courseType &&
      status == course.status &&
      lectureCount == course.lectureCount &&
      Objects.equals(name, course.name) &&
      Objects.equals(introduce, course.introduce) &&
      Objects.equals(shortIntro, course.shortIntro) &&
      Objects.equals(introduceImage, course.introduceImage) &&
      Objects.equals(note, course.note) &&
      Objects.equals(publishAt, course.publishAt) &&
      Objects.equals(price, course.price) &&
      Objects.equals(discountedPrice, course.discountedPrice) &&
      Objects.equals(offerTo, course.offerTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, accountId, name, introduce, shortIntro, introduceImage, note, createAt, publishAt, buyerCount, courseType, price, discountedPrice, offerTo, status, lectureCount);
  }
}
