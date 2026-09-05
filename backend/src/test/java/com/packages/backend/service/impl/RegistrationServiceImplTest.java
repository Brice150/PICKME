package com.packages.backend.service.impl;

import com.packages.backend.model.Registration;
import com.packages.backend.model.entity.GenderAge;
import com.packages.backend.model.entity.Geolocation;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.Gender;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.service.RegistrationResult;
import com.packages.backend.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistrationServiceImpl")
class RegistrationServiceImplTest {

  @Mock
  private UserService userService;

  @Captor
  private ArgumentCaptor<User> userCaptor;

  @InjectMocks
  private RegistrationServiceImpl registrationService;

  @Test
  @DisplayName("turns the registration form into a standard account")
  void registerTurnsTheFormIntoAStandardAccount() {
    Date birthDate = new Date(0);
    GenderAge genderAge = new GenderAge(Gender.MAN, Gender.WOMAN, 18L, 99L, null);
    Geolocation geolocation = new Geolocation("48.8566", "2.3522", 100L, null, null);
    Registration request = new Registration("nickname", "job", birthDate, "user@pickme.com", "password", genderAge, geolocation);
    when(userService.signUpUser(any(User.class))).thenReturn(new RegistrationResult.Created());

    RegistrationResult result = registrationService.register(request);

    assertThat(result).isEqualTo(new RegistrationResult.Created());
    verify(userService).signUpUser(userCaptor.capture());
    User created = userCaptor.getValue();
    assertThat(created.getUserRole()).isEqualTo(UserRole.ROLE_USER);
    assertThat(created.getNickname()).isEqualTo("nickname");
    assertThat(created.getJob()).isEqualTo("job");
    assertThat(created.getBirthDate()).isEqualTo(birthDate);
    assertThat(created.getEmail()).isEqualTo("user@pickme.com");
    assertThat(created.getPassword()).isEqualTo("password");
    assertThat(created.getGenderAge()).isSameAs(genderAge);
    assertThat(created.getGeolocation()).isSameAs(geolocation);
  }
}
