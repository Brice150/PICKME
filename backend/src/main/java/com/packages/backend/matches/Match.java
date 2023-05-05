package com.packages.backend.matches;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.packages.backend.user.User;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "matches")
public class Match implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  private Date date;
  @ManyToOne(optional = false)
  @JsonBackReference(value = "matchesSent")
  private User fkSender;
  @ManyToOne(optional = false)
  @JsonBackReference(value = "matchesReceived")
  private User fkReceiver;

  public Match() {
  }

  public Match(Date date, User fkSender, User fkReceiver) {
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

  public User getFkSender() {
    return fkSender;
  }

  public void setFkSender(User fkSender) {
    this.fkSender = fkSender;
  }

  public User getFkReceiver() {
    return fkReceiver;
  }

  public void setFkReceiver(User fkReceiver) {
    this.fkReceiver = fkReceiver;
  }
}

