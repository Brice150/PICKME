package com.packages.backend.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "likes")
public class Like implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  private Date date;
  private Long fkSender;
  private Long fkReceiver;

  public Like() {
  }

  public Like(Date date, Long fkSender, Long fkReceiver) {
    this.date = date;
    this.fkSender = fkSender;
    this.fkReceiver = fkReceiver;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Long getFkSender() {
    return fkSender;
  }

  public void setFkSender(Long fkSender) {
    this.fkSender = fkSender;
  }

  public Long getFkReceiver() {
    return fkReceiver;
  }

  public void setFkReceiver(Long fkReceiver) {
    this.fkReceiver = fkReceiver;
  }
}

