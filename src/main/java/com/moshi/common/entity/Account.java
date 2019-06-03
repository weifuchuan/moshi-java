package com.moshi.common.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Account {
  private int id;
  private String nickName;
  private String password;
  private String email;
  private String phone;
  private String avatar;
  private String realName;
  private String identityNumber;
  private Long age;
  private String company;
  private String position;
  private String personalProfile;
  private String sex;
  private Long birthday;
  private String education;
  private String profession;
  private long createAt;
  private int status;
  private String realPicture;



  @Id
  @Column(name = "id")
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  @Basic
  @Column(name = "nickName")
  public String getNickName() {
    return nickName;
  }

  public void setNickName(String nickName) {
    this.nickName = nickName;
  }

  @Basic
  @Column(name = "password")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Basic
  @Column(name = "email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Basic
  @Column(name = "phone")
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Basic
  @Column(name = "avatar")
  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  @Basic
  @Column(name = "realName")
  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }

  @Basic
  @Column(name = "identityNumber")
  public String getIdentityNumber() {
    return identityNumber;
  }

  public void setIdentityNumber(String identityNumber) {
    this.identityNumber = identityNumber;
  }

  @Basic
  @Column(name = "age")
  public Long getAge() {
    return age;
  }

  public void setAge(Long age) {
    this.age = age;
  }

  @Basic
  @Column(name = "company")
  public String getCompany() {
    return company;
  }

  public void setCompany(String company) {
    this.company = company;
  }

  @Basic
  @Column(name = "position")
  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  @Basic
  @Column(name = "personalProfile")
  public String getPersonalProfile() {
    return personalProfile;
  }

  public void setPersonalProfile(String personalProfile) {
    this.personalProfile = personalProfile;
  }

  @Basic
  @Column(name = "sex")
  public String getSex() {
    return sex;
  }

  public void setSex(String sex) {
    this.sex = sex;
  }

  @Basic
  @Column(name = "birthday")
  public Long getBirthday() {
    return birthday;
  }

  public void setBirthday(Long birthday) {
    this.birthday = birthday;
  }

  @Basic
  @Column(name = "education")
  public String getEducation() {
    return education;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  @Basic
  @Column(name = "profession")
  public String getProfession() {
    return profession;
  }

  public void setProfession(String profession) {
    this.profession = profession;
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
  @Column(name = "realPicture")
  public String getRealPicture() {
    return realPicture;
  }

  public void setRealPicture(String realPicture) {
    this.realPicture = realPicture;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Account account = (Account) o;
    return id == account.id &&
      createAt == account.createAt &&
      status == account.status &&
      Objects.equals(nickName, account.nickName) &&
      Objects.equals(password, account.password) &&
      Objects.equals(email, account.email) &&
      Objects.equals(phone, account.phone) &&
      Objects.equals(avatar, account.avatar) &&
      Objects.equals(realName, account.realName) &&
      Objects.equals(identityNumber, account.identityNumber) &&
      Objects.equals(age, account.age) &&
      Objects.equals(company, account.company) &&
      Objects.equals(position, account.position) &&
      Objects.equals(personalProfile, account.personalProfile) &&
      Objects.equals(sex, account.sex) &&
      Objects.equals(birthday, account.birthday) &&
      Objects.equals(education, account.education) &&
      Objects.equals(profession, account.profession) &&
      Objects.equals(realPicture, account.realPicture);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, nickName, password, email, phone, avatar, realName, identityNumber, age, company, position, personalProfile, sex, birthday, education, profession, createAt, status, realPicture);
  }
}
