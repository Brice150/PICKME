package com.packages.backend.model.user;

import javax.persistence.*;

@Entity
@Table(name = "stats")
public class Stats {
  @Id
  @Column(name = "user_id")
  private Long id;
  private Long totalDislikes;
  private Long totalLikes;
  private Long totalMatches;
  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  public Stats() {
  }

  public Stats(Long totalDislikes, Long totalLikes, Long totalMatches) {
    this.totalDislikes = totalDislikes;
    this.totalLikes = totalLikes;
    this.totalMatches = totalMatches;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
