package com.packages.backend.controller;

import com.packages.backend.model.Registration;
import com.packages.backend.service.RegistrationService;
import com.packages.backend.service.ServiceStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping()
  public ResponseEntity<String> register(@Valid @RequestBody Registration request) {
    String signUpMessage = registrationService.register(request);
    return ServiceStatus.OK.equals(signUpMessage) ?
      new ResponseEntity<>(HttpStatus.CREATED) :
      new ResponseEntity<>(signUpMessage, HttpStatus.FORBIDDEN);
  }
}
