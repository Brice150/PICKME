package com.packages.backend.model.user;

import com.packages.backend.model.user.enums.Gender;

import javax.persistence.*;

@Entity
@Table(name = "genders_ages")
public class GenderAge {
  @Id
  @Column(name = "user_id")
  private Long id;
  @Enumerated(EnumType.STRING)
  private Gender gender;
  @Enumerated(EnumType.STRING)
  private Gender genderSearch;
  private Long minAge;
  private Long maxAge;
  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  public GenderAge() {
  }

  public GenderAge(Gender gender, Gender genderSearch, Long minAge, Long maxAge) {
    this.gender = gender;
    this.genderSearch = genderSearch;
    this.minAge = minAge;
    this.maxAge = maxAge;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public Gender getGenderSearch() {
    return genderSearch;
  }

  public void setGenderSearch(Gender genderSearch) {
    this.genderSearch = genderSearch;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
