package com.packages.backend.model;

public class AdminStats {
  Long totalUsers;
  Long totalDeletedAccounts;
  Long totalRecentUsers;
  Long totalRecentDeletedAccounts;

  public AdminStats() {
  }

  public AdminStats(Long totalUsers, Long totalDeletedAccounts, Long totalRecentUsers, Long totalRecentDeletedAccounts) {
    this.totalUsers = totalUsers;
    this.totalDeletedAccounts = totalDeletedAccounts;
    this.totalRecentUsers = totalRecentUsers;
    this.totalRecentDeletedAccounts = totalRecentDeletedAccounts;
  }

  public Long getTotalUsers() {
    return totalUsers;
  }

  public void setTotalUsers(Long totalUsers) {
    this.totalUsers = totalUsers;
  }

  public Long getTotalDeletedAccounts() {
    return totalDeletedAccounts;
  }

  public void setTotalDeletedAccounts(Long totalDeletedAccounts) {
    this.totalDeletedAccounts = totalDeletedAccounts;
  }

  public Long getTotalRecentUsers() {
    return totalRecentUsers;
  }

  public void setTotalRecentUsers(Long totalRecentUsers) {
    this.totalRecentUsers = totalRecentUsers;
  }

  public Long getTotalRecentDeletedAccounts() {
    return totalRecentDeletedAccounts;
  }

  public void setTotalRecentDeletedAccounts(Long totalRecentDeletedAccounts) {
    this.totalRecentDeletedAccounts = totalRecentDeletedAccounts;
  }
}
