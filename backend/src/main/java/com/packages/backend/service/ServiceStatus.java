package com.packages.backend.service;

/**
 * Outcome of an operation the connected user is not always allowed to apply.
 */
public enum ServiceStatus {

  /** The operation has been applied. */
  OK,

  /** The connected user is not allowed to apply the operation. */
  FORBIDDEN
}
