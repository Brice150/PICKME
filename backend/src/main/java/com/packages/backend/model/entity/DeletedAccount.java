package com.packages.backend.model.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "deleted_accounts")
public class DeletedAccount {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  private String nickname;
  private String email;
  private Date registeredDate;
  private Date deletionDate;
  private Long totalDislikes;
  private Long totalLikes;
  private Long totalMatches;
  private String deletedBy;

  public DeletedAccount() {
  }

  public DeletedAccount(String nickname, String email, Date registeredDate, Date deletionDate, Long totalDislikes, Long totalLikes, Long totalMatches, String deletedBy) {
    this.nickname = nickname;
    this.email = email;
    this.registeredDate = registeredDate;
    this.deletionDate = deletionDate;
    this.totalDislikes = totalDislikes;
    this.totalLikes = totalLikes;
    this.totalMatches = totalMatches;
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

  public Date getRegisteredDate() {
    return registeredDate;
  }

  public void setRegisteredDate(Date registeredDate) {
    this.registeredDate = registeredDate;
  }

  public Date getDeletionDate() {
    return deletionDate;
  }

  public void setDeletionDate(Date deletionDate) {
    this.deletionDate = deletionDate;
  }

  public Long getTotalDislikes() {
    return totalDislikes;
  }

  public void setTotalDislikes(Long totalDislikes) {
    this.totalDislikes = totalDislikes;
  }

  public Long getTotalLikes() {
    return totalLikes;
  }

  public void setTotalLikes(Long totalLikes) {
    this.totalLikes = totalLikes;
  }

  public Long getTotalMatches() {
    return totalMatches;
  }

  public void setTotalMatches(Long totalMatches) {
    this.totalMatches = totalMatches;
  }

  public String getDeletedBy() {
    return deletedBy;
  }

  public void setDeletedBy(String deletedBy) {
    this.deletedBy = deletedBy;
  }
}
