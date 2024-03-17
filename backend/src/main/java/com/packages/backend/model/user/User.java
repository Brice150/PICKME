package com.packages.backend.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.packages.backend.model.Picture;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false, updatable = false)
  private Long id;
  @Enumerated(EnumType.STRING)
  private UserRole userRole;
  private Boolean locked = false;
  private Boolean enabled = true;
  private Date birthDate;
  private Boolean gold;


  // Pictures
  @OneToMany(mappedBy = "fkUser", cascade = CascadeType.ALL)
  @JsonManagedReference(value = "pictures")
  private List<Picture> pictures;
  private String mainPicture;


  // Main Infos
  private String nickname;
  private String job;
  private String city;
  private Long height;


  // Gender and Age
  private String gender;
  private String genderSearch;
  private Long minAge;
  private Long maxAge;


  // Connection Infos
  private String email;
  private String password;


  // Description
  private String description;


  // Preferences
  private String alcoholDrinking;
  private String smokes;
  private String organised;
  private String personality;
  private String sportPractice;
  private String animals;
  private String parenthood;
  private String gamer;

  public User() {
  }

  public User(UserRole userRole, Date birthDate, String nickname, String job, String city, String gender, String genderSearch, Long minAge, Long maxAge, String email, String password) {
    this.userRole = userRole;
    this.birthDate = birthDate;
    this.nickname = nickname;
    this.job = job;
    this.city = city;
    this.gender = gender;
    this.genderSearch = genderSearch;
    this.minAge = minAge;
    this.maxAge = maxAge;
    this.email = email;
    this.password = password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
    return Collections.singletonList(authority);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  public void setIsEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UserRole getUserRole() {
    return userRole;
  }

  public void setUserRole(UserRole userRole) {
    this.userRole = userRole;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public Boolean getGold() {
    return gold;
  }

  public void setGold(Boolean gold) {
    this.gold = gold;
  }

  public List<Picture> getPictures() {
    return pictures;
  }

  public void setPictures(List<Picture> pictures) {
    this.pictures = pictures;
  }

  public String getMainPicture() {
    return mainPicture;
  }

  public void setMainPicture(String mainPicture) {
    this.mainPicture = mainPicture;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getJob() {
    return job;
  }

  public void setJob(String job) {
    this.job = job;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Long getHeight() {
    return height;
  }

  public void setHeight(Long height) {
    this.height = height;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getGenderSearch() {
    return genderSearch;
  }

  public void setGenderSearch(String genderSearch) {
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAlcoholDrinking() {
    return alcoholDrinking;
  }

  public void setAlcoholDrinking(String alcoholDrinking) {
    this.alcoholDrinking = alcoholDrinking;
  }

  public String getSmokes() {
    return smokes;
  }

  public void setSmokes(String smokes) {
    this.smokes = smokes;
  }

  public String getOrganised() {
    return organised;
  }

  public void setOrganised(String organised) {
    this.organised = organised;
  }

  public String getPersonality() {
    return personality;
  }

  public void setPersonality(String personality) {
    this.personality = personality;
  }

  public String getSportPractice() {
    return sportPractice;
  }

  public void setSportPractice(String sportPractice) {
    this.sportPractice = sportPractice;
  }

  public String getAnimals() {
    return animals;
  }

  public void setAnimals(String animals) {
    this.animals = animals;
  }

  public String getParenthood() {
    return parenthood;
  }

  public void setParenthood(String parenthood) {
    this.parenthood = parenthood;
  }

  public String getGamer() {
    return gamer;
  }

  public void setGamer(String gamer) {
    this.gamer = gamer;
  }
}
