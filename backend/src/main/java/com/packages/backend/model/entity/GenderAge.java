package com.packages.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.packages.backend.model.enums.Gender;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "genders_ages")
public class GenderAge {
  @Id
  @Column(name = "user_id")
  private Long id;
  @NotNull(message = "Gender is empty")
  @Enumerated(EnumType.STRING)
  private Gender gender;
  @NotNull(message = "Gender search is empty")
  @Enumerated(EnumType.STRING)
  private Gender genderSearch;
  @NotNull(message = "Min age is empty")
  private Long minAge;
  @NotNull(message = "Max age is empty")
  private Long maxAge;
  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  @JsonBackReference(value = "genderAge")
  private User fkUser;

  public GenderAge() {
  }

  public GenderAge(Gender gender, Gender genderSearch, Long minAge, Long maxAge, User fkUser) {
    this.gender = gender;
    this.genderSearch = genderSearch;
    this.minAge = minAge;
    this.maxAge = maxAge;
    this.fkUser = fkUser;
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

  public User getFkUser() {
    return fkUser;
  }

  public void setFkUser(User fkUser) {
    this.fkUser = fkUser;
  }
}
