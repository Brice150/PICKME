package com.packages.backend.controller;

import com.packages.backend.model.Registration;
import com.packages.backend.service.RegistrationResult;
import com.packages.backend.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/registration")
public class RegistrationController {

  private final RegistrationService registrationService;

  public RegistrationController(RegistrationService registrationService) {
    this.registrationService = registrationService;
  }

  @PostMapping()
  public ResponseEntity<String> register(@Valid @RequestBody Registration request) {
    return switch (registrationService.register(request)) {
      case RegistrationResult.Created ignored -> new ResponseEntity<>(HttpStatus.CREATED);
      case RegistrationResult.Rejected(String reason) ->
        new ResponseEntity<>(reason, HttpStatus.FORBIDDEN);
    };
  }
}
