package com.packages.backend.model.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "deleted_accounts")
public class DeletedAccounts {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  private String nickname;
  private String email;
  private Date deletionDate;
  private String deletedBy;

  public DeletedAccounts() {
  }

  public DeletedAccounts(String nickname, String email, Date deletionDate, String deletedBy) {
    this.nickname = nickname;
    this.email = email;
    this.deletionDate = deletionDate;
    this.deletedBy = deletedBy;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Date getDeletionDate() {
    return deletionDate;
  }

  public void setDeletionDate(Date deletionDate) {
    this.deletionDate = deletionDate;
  }

  public String getDeletedBy() {
    return deletedBy;
  }

  public void setDeletedBy(String deletedBy) {
    this.deletedBy = deletedBy;
  }
}
