package com.packages.backend.service;

/**
 * Outcome of a registration. The rejection carries the reason the connection screen displays.
 */
public sealed interface RegistrationResult {

  /** The account has been created. */
  record Created() implements RegistrationResult {
  }

  /**
   * The account has not been created.
   *
   * @param reason message to display to the visitor
   */
  record Rejected(String reason) implements RegistrationResult {
  }
}
