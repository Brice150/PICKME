package com.packages.backend.repository;

import com.packages.backend.registration.token.ConfirmationToken;
import com.packages.backend.registration.token.ConfirmationTokenRepository;
import com.packages.backend.user.User;
import com.packages.backend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ConfirmationTokenRepoTest {
  @Autowired
  private ConfirmationTokenRepository confirmationTokenRepository;
  @Autowired
  private UserRepository userRepository;
  private User user1;

  @BeforeEach
  void setUp() {
    user1 = new User();
    user1.setEmail("user1@gmail.com");
    user1.setNickname("user1");
    user1.setPassword("password");

    userRepository.save(user1);
  }

  @Test
  void testFindByToken() {
    // Given
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = new ConfirmationToken(
      token,
      LocalDateTime.now(),
      LocalDateTime.now().plusMinutes(15),
      user1
    );

    confirmationTokenRepository.save(confirmationToken);

    // When
    Optional<ConfirmationToken> result = confirmationTokenRepository.findByToken(token);

    // Then
    assertThat(result).isPresent().contains(confirmationToken);
  }

  @Test
  void testUpdateConfirmedAt() {
    // Given
    String token = UUID.randomUUID().toString();
    ConfirmationToken confirmationToken = new ConfirmationToken(
      token,
      LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
      LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(15),
      user1
    );

    confirmationTokenRepository.save(confirmationToken);
    LocalDateTime confirmedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).plusMinutes(7);
    // When
    int result = confirmationTokenRepository.updateConfirmedAt(token, confirmedAt);

    // Then
    assertThat(result).isEqualTo(1);
    Optional<ConfirmationToken> updatedToken = confirmationTokenRepository.findByToken(token);
    assertThat(updatedToken).isPresent();
    assertThat(updatedToken.get().getConfirmedAt()).isEqualTo(confirmedAt);
  }
}
