package com.moshi.common.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "preset_text", schema = "moshi2", catalog = "")
public class PresetText {
  private String key;
  private String value;
  private String type;

  @Id
  @Column(name = "key")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  @Basic
  @Column(name = "value")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Basic
  @Column(name = "type")
  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PresetText that = (PresetText) o;
    return Objects.equals(key, that.key) &&
      Objects.equals(value, that.value) &&
      Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, value, type);
  }
}
