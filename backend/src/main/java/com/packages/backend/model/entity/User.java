package com.packages.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.packages.backend.model.enums.UserRole;
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
  private String nickname;
  private String job;
  private Long height;
  private String email;
  private String password;
  @Column(columnDefinition = "VARCHAR", length = -1)
  private String description;
  @OneToOne(mappedBy = "fkUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @PrimaryKeyJoinColumn
  @JsonManagedReference(value = "genderAge")
  private GenderAge genderAge;
  @OneToOne(mappedBy = "fkUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @PrimaryKeyJoinColumn
  @JsonManagedReference(value = "preferences")
  private Preferences preferences;
  @OneToOne(mappedBy = "fkUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @PrimaryKeyJoinColumn
  @JsonManagedReference(value = "geolocation")
  private Geolocation geolocation;
  @OneToMany(mappedBy = "fkUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @OrderBy("isMainPicture DESC, id DESC")
  @JsonManagedReference(value = "pictures")
  private List<Picture> pictures;
  @OneToOne(mappedBy = "fkUser", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @PrimaryKeyJoinColumn
  @JsonManagedReference(value = "stats")
  private Stats stats;

  public User() {
  }

  public User(UserRole userRole, Date birthDate, String nickname, String job, String email, String password, GenderAge genderAge, Geolocation geolocation) {
    this.userRole = userRole;
    this.birthDate = birthDate;
    this.nickname = nickname;
    this.job = job;
    this.email = email;
    this.password = password;
    this.genderAge = genderAge;
    this.geolocation = geolocation;
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

  public Long getHeight() {
    return height;
  }

  public void setHeight(Long height) {
    this.height = height;
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

  public GenderAge getGenderAge() {
    return genderAge;
  }

  public void setGenderAge(GenderAge genderAge) {
    this.genderAge = genderAge;
  }

  public Preferences getPreferences() {
    return preferences;
  }

  public void setPreferences(Preferences preferences) {
    this.preferences = preferences;
  }

  public Geolocation getGeolocation() {
    return geolocation;
  }

  public void setGeolocation(Geolocation geolocation) {
    this.geolocation = geolocation;
  }

  public List<Picture> getPictures() {
    return pictures;
  }

  public void setPictures(List<Picture> pictures) {
    this.pictures = pictures;
  }

  public Stats getStats() {
    return stats;
  }

  public void setStats(Stats stats) {
    this.stats = stats;
  }
}
