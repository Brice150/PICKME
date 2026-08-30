package com.packages.backend.service;

import com.packages.backend.model.Registration;

public interface RegistrationService {

  /**
   * Creates an account from the registration form.
   *
   * @param request registration form
   * @return {@link ServiceStatus#OK} when the account has been created, otherwise the reason of
   * the rejection
   */
  String register(Registration request);
}
