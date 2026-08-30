package com.packages.backend.service;

public final class ServiceStatus {

  // The operation has been applied.
  public static final String OK = "OK";

  // The connected user is not allowed to apply the operation.
  public static final String FORBIDDEN = "FORBIDDEN";

  private ServiceStatus() {
  }
}
