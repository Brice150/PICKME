package com.packages.backend.model.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.packages.backend.model.Picture;
import com.packages.backend.model.user.enums.*;
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


  // Geolocation
  private String city;
  private String latitude;
  private String longitude;
  private Long distance;


  // Pictures
  @OneToMany(mappedBy = "fkUser", cascade = CascadeType.ALL)
  @OrderBy("isMainPicture DESC, id DESC")
  @JsonManagedReference(value = "pictures")
  private List<Picture> pictures;


  // Main Infos
  private String nickname;
  private String job;
  private Long distanceSearch;
  private Long height;


  // Gender and Age
  @Enumerated(EnumType.STRING)
  private Gender gender;
  @Enumerated(EnumType.STRING)
  private Gender genderSearch;
  private Long minAge;
  private Long maxAge;


  // Connection Infos
  private String email;
  private String password;


  // Description
  private String description;


  // Preferences
  @Enumerated(EnumType.STRING)
  private AlcoholDrinking alcoholDrinking;
  @Enumerated(EnumType.STRING)
  private Smokes smokes;
  @Enumerated(EnumType.STRING)
  private Organised organised;
  @Enumerated(EnumType.STRING)
  private Personality personality;
  @Enumerated(EnumType.STRING)
  private SportPractice sportPractice;
  @Enumerated(EnumType.STRING)
  private Animals animals;
  @Enumerated(EnumType.STRING)
  private Parenthood parenthood;
  @Enumerated(EnumType.STRING)
  private Gamer gamer;


  // Stats
  private Long totalDislikes;
  private Long totalLikes;
  private Long totalMatches;

  public User() {
  }

  public User(UserRole userRole, Date birthDate, String nickname, String job, Long distanceSearch, String city, String latitude, String longitude, Gender gender, Gender genderSearch, Long minAge, Long maxAge, String email, String password) {
    this.userRole = userRole;
    this.birthDate = birthDate;
    this.nickname = nickname;
    this.job = job;
    this.distanceSearch = distanceSearch;
    this.city = city;
    this.latitude = latitude;
    this.longitude = longitude;
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

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getLatitude() {
    return latitude;
  }

  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }

  public String getLongitude() {
    return longitude;
  }

  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  public Long getDistance() {
    return distance;
  }

  public void setDistance(Long distance) {
    this.distance = distance;
  }

  public List<Picture> getPictures() {
    return pictures;
  }

  public void setPictures(List<Picture> pictures) {
    this.pictures = pictures;
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

  public Long getDistanceSearch() {
    return distanceSearch;
  }

  public void setDistanceSearch(Long distanceSearch) {
    this.distanceSearch = distanceSearch;
  }

  public Long getHeight() {
    return height;
  }

  public void setHeight(Long height) {
    this.height = height;
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

  public AlcoholDrinking getAlcoholDrinking() {
    return alcoholDrinking;
  }

  public void setAlcoholDrinking(AlcoholDrinking alcoholDrinking) {
    this.alcoholDrinking = alcoholDrinking;
  }

  public Smokes getSmokes() {
    return smokes;
  }

  public void setSmokes(Smokes smokes) {
    this.smokes = smokes;
  }

  public Organised getOrganised() {
    return organised;
  }

  public void setOrganised(Organised organised) {
    this.organised = organised;
  }

  public Personality getPersonality() {
    return personality;
  }

  public void setPersonality(Personality personality) {
    this.personality = personality;
  }

  public SportPractice getSportPractice() {
    return sportPractice;
  }

  public void setSportPractice(SportPractice sportPractice) {
    this.sportPractice = sportPractice;
  }

  public Animals getAnimals() {
    return animals;
  }

  public void setAnimals(Animals animals) {
    this.animals = animals;
  }

  public Parenthood getParenthood() {
    return parenthood;
  }

  public void setParenthood(Parenthood parenthood) {
    this.parenthood = parenthood;
  }

  public Gamer getGamer() {
    return gamer;
  }

  public void setGamer(Gamer gamer) {
    this.gamer = gamer;
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
}
