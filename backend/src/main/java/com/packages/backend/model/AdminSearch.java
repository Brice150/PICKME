package com.packages.backend.model;

import com.packages.backend.model.user.enums.Gender;

import java.util.Set;

public class AdminSearch {
  private String nickname;
  private Set<Gender> genders;
  private Long minAge;
  private Long maxAge;

  public AdminSearch() {
  }

  public AdminSearch(String nickname, Set<Gender> genders, Long minAge, Long maxAge) {
    this.nickname = nickname;
    this.genders = genders;
    this.minAge = minAge;
    this.maxAge = maxAge;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public Set<Gender> getGenders() {
    return genders;
  }

  public void setGenders(Set<Gender> genders) {
    this.genders = genders;
  }

  public Long getMinAge() {
    return minAge;
  }

  public void setMinAge(Long minAge) {
    this.minAge = minAge;
  }

  public Long getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(Long maxAge) {
    this.maxAge = maxAge;
  }
}
