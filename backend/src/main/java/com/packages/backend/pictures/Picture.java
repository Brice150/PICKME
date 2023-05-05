package com.packages.backend.pictures;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.packages.backend.user.User;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "pictures")
public class Picture implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  private String content;
  @ManyToOne(optional = false)
  @JsonBackReference(value = "pictures")
  private User fkUser;

  public Picture() {
  }

  public Picture(String content, User fkUser) {
    this.content = content;
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

  public User getFkUser() {
    return fkUser;
  }

  public void setFkUser(User fkUser) {
    this.fkUser = fkUser;
  }
}

