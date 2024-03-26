package com.packages.backend.model.user;

import javax.persistence.*;

@Entity
@Table(name = "geolocations")
public class Geolocation {
  @Id
  @Column(name = "user_id")
  private Long id;
  private String city;
  private String latitude;
  private String longitude;
  private Long distanceSearch;
  private Long distance;
  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  public Geolocation() {
  }

  public Geolocation(String city, String latitude, String longitude, Long distanceSearch, Long distance) {
    this.city = city;
    this.latitude = latitude;
    this.longitude = longitude;
    this.distanceSearch = distanceSearch;
    this.distance = distance;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
