package com.packages.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pictures")
public class Picture implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  @Column(columnDefinition = "VARCHAR", length = -1)
  private String content;
  private Boolean isMainPicture;
  @ManyToOne(optional = false)
  @JsonBackReference(value = "pictures")
  private User fkUser;

  public Picture() {
  }

  public Picture(String content, Boolean isMainPicture, User fkUser) {
    this.content = content;
    this.isMainPicture = isMainPicture;
    this.fkUser = fkUser;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Boolean getIsMainPicture() {
    return isMainPicture;
  }

  public void setIsMainPicture(Boolean mainPicture) {
    isMainPicture = mainPicture;
  }

  public User getFkUser() {
    return fkUser;
  }

  public void setFkUser(User fkUser) {
    this.fkUser = fkUser;
  }
}

