package com.packages.backend.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  private String content;
  private Date date;
  private String sender;
  private Long fkSender;
  private Long fkReceiver;

  public Message() {
  }

  public Message(String content, Date date, String sender, Long fkSender, Long fkReceiver) {
    this.content = content;
    this.date = date;
    this.sender = sender;
    this.fkSender = fkSender;
    this.fkReceiver = fkReceiver;
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

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
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

