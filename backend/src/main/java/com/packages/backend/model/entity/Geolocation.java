package com.packages.backend.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "geolocations")
public class Geolocation {
  @Id
  @Column(name = "user_id")
  private Long id;
  @NotBlank(message = "Latitude is empty")
  private String latitude;
  @NotBlank(message = "Longitude is empty")
  private String longitude;
  @NotNull(message = "Max Distance is empty")
  private Long distanceSearch;
  private Long distance;
  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  @JsonBackReference(value = "geolocation")
  private User fkUser;

  public Geolocation() {
  }

  public Geolocation(String latitude, String longitude, Long distanceSearch, Long distance, User fkUser) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.distanceSearch = distanceSearch;
    this.distance = distance;
    this.fkUser = fkUser;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Long getDistanceSearch() {
    return distanceSearch;
  }

  public void setDistanceSearch(Long distanceSearch) {
    this.distanceSearch = distanceSearch;
  }

  public Long getDistance() {
    return distance;
  }

  public void setDistance(Long distance) {
    this.distance = distance;
  }

  public User getFkUser() {
    return fkUser;
  }

  public void setFkUser(User fkUser) {
    this.fkUser = fkUser;
  }
}
