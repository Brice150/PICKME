package com.packages.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "notifications")
public class Notification {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  private String content;
  private String link;
  private Date date;
  private Boolean seen;
  @ManyToOne(optional = false)
  @JsonBackReference(value = "notifications")
  private User fkUser;

  public Notification() {
  }

  public Notification(String content, String link, Date date, Boolean seen, User fkUser) {
    this.content = content;
    this.link = link;
    this.date = date;
    this.seen = seen;
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

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Boolean getSeen() {
    return seen;
  }

  public void setSeen(Boolean seen) {
    this.seen = seen;
  }

  public User getFkUser() {
    return fkUser;
  }

  public void setFkUser(User fkUser) {
    this.fkUser = fkUser;
  }
}
