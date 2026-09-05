package com.packages.backend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("BackendApplication")
class BackendApplicationTests {

  @Test
  @DisplayName("starts with every bean of the application wired together")
  void contextLoads() {
  }
}
