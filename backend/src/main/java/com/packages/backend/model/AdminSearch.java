package com.packages.backend.model;

public class AdminSearch {
  private String email;
  private String orderBy;

  public AdminSearch() {
  }

  public AdminSearch(String email, String orderBy) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }
}
