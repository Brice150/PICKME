package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.exception.UserNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.dto.UserDTOMapperRestricted;
import com.packages.backend.model.dto.UserUpdateRequest;
import com.packages.backend.model.entity.GenderAge;
import com.packages.backend.model.entity.Geolocation;
import com.packages.backend.model.entity.Message;
import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.Preferences;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.AlcoholDrinking;
import com.packages.backend.model.enums.Animals;
import com.packages.backend.model.enums.Gamer;
import com.packages.backend.model.enums.Gender;
import com.packages.backend.model.enums.Organised;
import com.packages.backend.model.enums.Parenthood;
import com.packages.backend.model.enums.Personality;
import com.packages.backend.model.enums.Smokes;
import com.packages.backend.model.enums.SportPractice;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.repository.NotificationRepository;
import com.packages.backend.repository.PictureRepository;
import com.packages.backend.repository.UserRepository;
import com.packages.backend.service.DeletedAccountService;
import com.packages.backend.service.DistanceService;
import com.packages.backend.service.ServiceStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

  private static final String CONNECTED_EMAIL = "user1@pickme.com";

  @Mock
  private UserRepository userRepository;
  @Mock
  private MessageRepository messageRepository;
  @Mock
  private LikeRepository likeRepository;
  @Mock
  private PictureRepository pictureRepository;
  @Mock
  private NotificationRepository notificationRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private DistanceService distanceService;
  @Mock
  private DeletedAccountService deletedAccountService;

  @Captor
  private ArgumentCaptor<User> userCaptor;
  @Captor
  private ArgumentCaptor<List<Notification>> notificationsCaptor;

  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
    userService = new UserServiceImpl(userRepository, messageRepository, likeRepository, pictureRepository,
      notificationRepository, passwordEncoder, new UserDTOMapper(), new UserDTOMapperRestricted(),
      distanceService, deletedAccountService);
  }

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  /**
   * Authenticates an account for the calls that read the security context.
   *
   * @param user account to authenticate
   */
  private void authenticate(User user) {
    SecurityContextHolder.getContext()
      .setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
    when(userRepository.getUserByEmail(user.getEmail())).thenReturn(Optional.of(user));
  }

  @Nested
  @DisplayName("authentication")
  class Authentication {

    @Test
    @DisplayName("loads an account from its email")
    void loadUserByUsernameReturnsTheAccount() {
      User user = TestFixtures.user(1L);
      when(userRepository.getUserByEmail(CONNECTED_EMAIL)).thenReturn(Optional.of(user));

      assertThat(userService.loadUserByUsername(CONNECTED_EMAIL)).isSameAs(user);
    }

    @Test
    @DisplayName("rejects an email that owns no account")
    void loadUserByUsernameRejectsAnUnknownEmail() {
      when(userRepository.getUserByEmail("ghost@pickme.com")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.loadUserByUsername("ghost@pickme.com"))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessage("user with email ghost@pickme.com not found");
    }

    @Test
    @DisplayName("returns the account owning the security context")
    void getConnectedUserReturnsTheAuthenticatedAccount() {
      User connectedUser = TestFixtures.user(1L);
      authenticate(connectedUser);

      assertThat(userService.getConnectedUser()).isSameAs(connectedUser);
    }

    @Test
    @DisplayName("exposes the complete view of the connected account")
    void getConnectedUserDTOExposesTheCompleteView() {
      User connectedUser = TestFixtures.user(1L);
      authenticate(connectedUser);

      UserDTO connectedUserDTO = userService.getConnectedUserDTO();

      assertThat(connectedUserDTO.id()).isEqualTo(1L);
      assertThat(connectedUserDTO.userRole()).isEqualTo(UserRole.ROLE_USER);
      assertThat(connectedUserDTO.email()).isEqualTo(CONNECTED_EMAIL);
      assertThat(connectedUserDTO.stats()).isSameAs(connectedUser.getStats());
    }
  }

  @Nested
  @DisplayName("registration")
  class Registration {

    @Test
    @DisplayName("hashes the password and initialises the sub entities of a new account")
    void signUpUserRegistersTheAccount() {
      User user = TestFixtures.user(1L);
      user.setPreferences(null);
      user.setStats(null);
      user.setRegisteredDate(null);
      when(userRepository.getUserByEmail(CONNECTED_EMAIL)).thenReturn(Optional.empty());
      when(passwordEncoder.encode("password1")).thenReturn("hashed");

      assertThat(userService.signUpUser(user)).isEqualTo(ServiceStatus.OK);

      verify(userRepository).save(userCaptor.capture());
      User saved = userCaptor.getValue();
      assertThat(saved.getPassword()).isEqualTo("hashed");
      assertThat(saved.getRegisteredDate()).isNotNull();
      assertThat(saved.getGenderAge().getFkUser()).isSameAs(saved);
      assertThat(saved.getGeolocation().getFkUser()).isSameAs(saved);
      assertThat(saved.getPreferences().getFkUser()).isSameAs(saved);
      assertThat(saved.getStats().getTotalLikes()).isZero();
      assertThat(saved.getStats().getTotalDislikes()).isZero();
      assertThat(saved.getStats().getTotalMatches()).isZero();
    }

    @Test
    @DisplayName("rejects an email that already owns an account")
    void signUpUserRejectsATakenEmail() {
      User user = TestFixtures.user(1L);
      when(userRepository.getUserByEmail(CONNECTED_EMAIL)).thenReturn(Optional.of(TestFixtures.user(2L)));

      assertThat(userService.signUpUser(user)).isEqualTo("Email already taken");
      verify(userRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("lookup")
  class Lookup {

    @Test
    @DisplayName("finds an account from its identifier")
    void getUserByIdReturnsTheAccount() {
      User user = TestFixtures.user(2L);
      when(userRepository.findById(2L)).thenReturn(Optional.of(user));

      assertThat(userService.getUserById(2L)).isSameAs(user);
    }

    @Test
    @DisplayName("fails when no account matches the identifier")
    void getUserByIdFailsWhenTheAccountIsUnknown() {
      when(userRepository.findById(2L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.getUserById(2L))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User by id 2 was not found");
    }

    @Test
    @DisplayName("finds an account from its email")
    void getUserByEmailReturnsTheAccount() {
      User user = TestFixtures.user(1L);
      when(userRepository.getUserByEmail(CONNECTED_EMAIL)).thenReturn(Optional.of(user));

      assertThat(userService.getUserByEmail(CONNECTED_EMAIL)).isSameAs(user);
    }

    @Test
    @DisplayName("fails when no account matches the email")
    void getUserByEmailFailsWhenTheAccountIsUnknown() {
      when(userRepository.getUserByEmail("ghost@pickme.com")).thenReturn(Optional.empty());

      assertThatThrownBy(() -> userService.getUserByEmail("ghost@pickme.com"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User by email ghost@pickme.com was not found");
    }
  }

  @Nested
  @DisplayName("selection")
  class Selection {

    @Test
    @DisplayName("keeps the candidates within the search radius and attaches their main picture")
    void getAllSelectedUsersKeepsTheCandidatesWithinTheRadius() {
      User connectedUser = connectedUserWithPartialPreferences();
      User closeCandidate = candidateWithPreferences(2L);
      User otherCloseCandidate = candidateWithPreferences(3L);
      User farCandidate = candidateWithPreferences(4L);
      authenticate(connectedUser);
      when(userRepository.getAllUsers(any(Gender.class), any(Gender.class), any(Date.class), any(Date.class), eq(1L)))
        .thenReturn(List.of(closeCandidate, otherCloseCandidate, farCandidate));
      when(likeRepository.getGoldByConnectedUserId(1L)).thenReturn(List.of(3L));
      when(distanceService.calculateDistance(connectedUser, closeCandidate)).thenReturn(5.0);
      when(distanceService.calculateDistance(connectedUser, otherCloseCandidate)).thenReturn(8.0);
      when(distanceService.calculateDistance(connectedUser, farCandidate)).thenReturn(500.0);
      Picture mainPicture = new Picture("content", true, closeCandidate);
      when(pictureRepository.findDisplayedPicturesByUserIds(List.of(2L, 3L))).thenReturn(List.of(mainPicture));

      List<UserDTO> candidates = userService.getAllSelectedUsers(null);

      assertThat(candidates).extracting(UserDTO::id).containsExactly(2L, 3L);
      assertThat(candidates.get(0).pictures()).containsExactly(mainPicture);
      assertThat(candidates.get(1).pictures()).isEmpty();
      assertThat(candidates).allSatisfy(candidate -> assertThat(candidate.userRole()).isEqualTo(UserRole.HIDDEN));
      assertThat(closeCandidate.getGold()).isFalse();
      assertThat(otherCloseCandidate.getGold()).isTrue();
    }

    @Test
    @DisplayName("selects the age range from the birth dates of the candidates")
    void getAllSelectedUsersSelectsOnBirthDates() {
      User connectedUser = connectedUserWithPartialPreferences();
      connectedUser.getGenderAge().setMinAge(25L);
      connectedUser.getGenderAge().setMaxAge(35L);
      authenticate(connectedUser);
      ArgumentCaptor<Date> earliest = ArgumentCaptor.forClass(Date.class);
      ArgumentCaptor<Date> latest = ArgumentCaptor.forClass(Date.class);
      when(userRepository.getAllUsers(any(Gender.class), any(Gender.class), earliest.capture(), latest.capture(), eq(1L)))
        .thenReturn(List.of());
      when(likeRepository.getGoldByConnectedUserId(1L)).thenReturn(List.of());

      assertThat(userService.getAllSelectedUsers(-1)).isEmpty();

      LocalDate today = LocalDate.now();
      assertThat(toLocalDate(earliest.getValue())).isEqualTo(today.minusYears(36).plusDays(1));
      assertThat(toLocalDate(latest.getValue())).isEqualTo(today.minusYears(25).plusDays(1));
    }

    @Test
    @DisplayName("returns nothing beyond the last page of candidates")
    void getAllSelectedUsersReturnsNothingBeyondTheLastPage() {
      User connectedUser = connectedUserWithPartialPreferences();
      User candidate = candidateWithPreferences(2L);
      authenticate(connectedUser);
      when(userRepository.getAllUsers(any(Gender.class), any(Gender.class), any(Date.class), any(Date.class), eq(1L)))
        .thenReturn(List.of(candidate));
      when(likeRepository.getGoldByConnectedUserId(1L)).thenReturn(List.of());
      when(distanceService.calculateDistance(connectedUser, candidate)).thenReturn(5.0);

      assertThat(userService.getAllSelectedUsers(1)).isEmpty();
      verify(pictureRepository, never()).findDisplayedPicturesByUserIds(anyList());
    }

    /**
     * Builds the connected user with a preference left empty, one the candidates leave empty and
     * one both fill in, so that the three ways of scoring a preference are exercised.
     *
     * @return the connected user
     */
    private User connectedUserWithPartialPreferences() {
      User connectedUser = TestFixtures.user(1L);
      Preferences preferences = connectedUser.getPreferences();
      preferences.setPersonality(null);
      preferences.setParenthood(Parenthood.YES);
      preferences.setSmokes(Smokes.NO);
      preferences.setOrganised(Organised.YES);
      preferences.setSportPractice(SportPractice.YES);
      preferences.setAnimals(Animals.YES);
      preferences.setAlcoholDrinking(AlcoholDrinking.MAYBE);
      preferences.setGamer(Gamer.NO);
      return connectedUser;
    }

    private User candidateWithPreferences(Long id) {
      User candidate = TestFixtures.user(id);
      Preferences preferences = candidate.getPreferences();
      preferences.setPersonality(Personality.EXTRAVERT);
      preferences.setParenthood(null);
      preferences.setSmokes(Smokes.NO);
      preferences.setOrganised(Organised.YES);
      preferences.setSportPractice(SportPractice.YES);
      preferences.setAnimals(Animals.YES);
      preferences.setAlcoholDrinking(AlcoholDrinking.MAYBE);
      preferences.setGamer(Gamer.NO);
      return candidate;
    }

    private LocalDate toLocalDate(Date date) {
      return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

  }

  @Nested
  @DisplayName("matches")
  class Matches {

    @Test
    @DisplayName("attaches the conversation to every match of the connected user")
    void getAllUserMatchesAttachesTheConversations() {
      User connectedUser = TestFixtures.user(1L);
      User matchedUser = TestFixtures.user(2L);
      List<Message> conversation = List.of(new Message("hello", new Date(), "nickname1", 1L, 2L));
      authenticate(connectedUser);
      when(userRepository.getAllUserMatches(1L)).thenReturn(List.of(matchedUser));
      when(distanceService.calculateDistance(connectedUser, matchedUser)).thenReturn(12.9);
      when(messageRepository.getUserMessagesByFk(1L, 2L)).thenReturn(conversation);

      List<Match> matches = userService.getAllUserMatches();

      assertThat(matches).hasSize(1);
      assertThat(matches.get(0).getUser().id()).isEqualTo(2L);
      assertThat(matches.get(0).getUser().userRole()).isEqualTo(UserRole.HIDDEN);
      assertThat(matches.get(0).getMessages()).isEqualTo(conversation);
      assertThat(matchedUser.getGeolocation().getDistance()).isEqualTo(12L);
    }
  }

  @Nested
  @DisplayName("profile update")
  class ProfileUpdate {

    @Test
    @DisplayName("returns the account untouched when nothing is submitted")
    void updateUserReturnsTheAccountWhenNothingIsSubmitted() {
      User connectedUser = TestFixtures.user(1L);
      authenticate(connectedUser);

      assertThat(userService.updateUser(null).id()).isEqualTo(1L);
      verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("ignores the fields left out of the request")
    void updateUserIgnoresTheMissingFields() {
      User connectedUser = TestFixtures.user(1L);
      authenticate(connectedUser);
      when(userRepository.save(connectedUser)).thenReturn(connectedUser);

      userService.updateUser(new UserUpdateRequest());

      assertThat(connectedUser.getNickname()).isEqualTo("nickname1");
      assertThat(connectedUser.getJob()).isEqualTo("job1");
      assertThat(connectedUser.getHeight()).isEqualTo(180L);
      assertThat(connectedUser.getDescription()).isEqualTo("description1");
      assertThat(connectedUser.getGenderAge().getGender()).isEqualTo(Gender.MAN);
      verify(passwordEncoder, never()).encode(any());
    }

    @Test
    @DisplayName("keeps the previous values of the empty fields of the submitted sub objects")
    void updateUserKeepsThePreviousValuesOfEmptyFields() {
      User connectedUser = TestFixtures.user(1L);
      connectedUser.getPreferences().setSmokes(Smokes.NO);
      authenticate(connectedUser);
      when(userRepository.save(connectedUser)).thenReturn(connectedUser);
      UserUpdateRequest request = new UserUpdateRequest();
      request.setNickname("nickname1");
      request.setJob("job1");
      request.setHeight(180L);
      request.setDescription("description1");
      request.setGenderAge(new GenderAge());
      request.setPreferences(new Preferences());
      request.setGeolocation(new Geolocation());

      userService.updateUser(request);

      assertThat(connectedUser.getNickname()).isEqualTo("nickname1");
      assertThat(connectedUser.getGenderAge().getGender()).isEqualTo(Gender.MAN);
      assertThat(connectedUser.getGenderAge().getGenderSearch()).isEqualTo(Gender.WOMAN);
      assertThat(connectedUser.getGenderAge().getMinAge()).isEqualTo(18L);
      assertThat(connectedUser.getGenderAge().getMaxAge()).isEqualTo(99L);
      assertThat(connectedUser.getPreferences().getSmokes()).isEqualTo(Smokes.NO);
      assertThat(connectedUser.getGeolocation().getLatitude()).isEqualTo(TestFixtures.PARIS_LATITUDE);
      assertThat(connectedUser.getGeolocation().getLongitude()).isEqualTo(TestFixtures.PARIS_LONGITUDE);
      assertThat(connectedUser.getGeolocation().getDistanceSearch()).isEqualTo(100L);
    }

    @Test
    @DisplayName("applies every submitted field and hashes the new password")
    void updateUserAppliesEverySubmittedField() {
      User connectedUser = TestFixtures.user(1L);
      authenticate(connectedUser);
      when(userRepository.save(connectedUser)).thenReturn(connectedUser);
      when(passwordEncoder.encode("newPassword")).thenReturn("hashed");
      UserUpdateRequest request = new UserUpdateRequest();
      request.setNickname("newNickname");
      request.setJob("newJob");
      request.setHeight(175L);
      request.setDescription("newDescription");
      request.setPassword("newPassword");
      request.setGenderAge(new GenderAge(Gender.WOMAN, Gender.MAN, 20L, 40L, null));
      request.setPreferences(new Preferences(AlcoholDrinking.NO, Smokes.YES, Organised.NO,
        Personality.INTROVERT, SportPractice.NO, Animals.NO, Parenthood.NO, Gamer.YES, null));
      request.setGeolocation(new Geolocation("45.7640", "4.8357", 50L, null, null));

      UserDTO updated = userService.updateUser(request);

      assertThat(updated.nickname()).isEqualTo("newNickname");
      assertThat(connectedUser.getJob()).isEqualTo("newJob");
      assertThat(connectedUser.getHeight()).isEqualTo(175L);
      assertThat(connectedUser.getDescription()).isEqualTo("newDescription");
      assertThat(connectedUser.getGenderAge().getGender()).isEqualTo(Gender.WOMAN);
      assertThat(connectedUser.getGenderAge().getGenderSearch()).isEqualTo(Gender.MAN);
      assertThat(connectedUser.getGenderAge().getMinAge()).isEqualTo(20L);
      assertThat(connectedUser.getGenderAge().getMaxAge()).isEqualTo(40L);
      assertThat(connectedUser.getPreferences().getAlcoholDrinking()).isEqualTo(AlcoholDrinking.NO);
      assertThat(connectedUser.getPreferences().getSmokes()).isEqualTo(Smokes.YES);
      assertThat(connectedUser.getPreferences().getOrganised()).isEqualTo(Organised.NO);
      assertThat(connectedUser.getPreferences().getPersonality()).isEqualTo(Personality.INTROVERT);
      assertThat(connectedUser.getPreferences().getSportPractice()).isEqualTo(SportPractice.NO);
      assertThat(connectedUser.getPreferences().getAnimals()).isEqualTo(Animals.NO);
      assertThat(connectedUser.getPreferences().getParenthood()).isEqualTo(Parenthood.NO);
      assertThat(connectedUser.getPreferences().getGamer()).isEqualTo(Gamer.YES);
      assertThat(connectedUser.getGeolocation().getLatitude()).isEqualTo("45.7640");
      assertThat(connectedUser.getGeolocation().getLongitude()).isEqualTo("4.8357");
      assertThat(connectedUser.getGeolocation().getDistanceSearch()).isEqualTo(50L);
      assertThat(connectedUser.getPassword()).isEqualTo("hashed");
    }
  }

  @Nested
  @DisplayName("account deletion")
  class AccountDeletion {

    @Test
    @DisplayName("removes everything the account owns and warns its matches")
    void deleteConnectedUserRemovesEverythingItOwns() {
      User connectedUser = TestFixtures.user(1L);
      User matchedUser = TestFixtures.user(2L);
      authenticate(connectedUser);
      when(userRepository.getAllUserMatches(1L)).thenReturn(List.of(matchedUser));

      userService.deleteConnectedUser();

      verify(notificationRepository).saveAll(notificationsCaptor.capture());
      assertThat(notificationsCaptor.getValue()).singleElement().satisfies(notification -> {
        assertThat(notification.getContent()).isEqualTo("nickname1 has deleted his account");
        assertThat(notification.getLink()).isEqualTo("delete");
        assertThat(notification.getSeen()).isFalse();
        assertThat(notification.getFkUser()).isSameAs(matchedUser);
      });
      verify(userRepository).deleteUserNotificationsByFk(1L);
      verify(userRepository).deleteUserGeolocationByFk(1L);
      verify(userRepository).deleteUserPreferencesByFk(1L);
      verify(userRepository).deleteUserGenderAgeByFk(1L);
      verify(userRepository).deleteUserStatsByFk(1L);
      verify(userRepository).deleteUserPicturesByFk(1L);
      verify(userRepository).deleteUserLikesByFk(1L);
      verify(userRepository).deleteUserDislikesByFk(1L);
      verify(userRepository).deleteUserMessagesByFk(1L);
      verify(userRepository).deleteUserByEmail(CONNECTED_EMAIL);
      verify(deletedAccountService).addDeletedAccount(connectedUser, connectedUser);
    }

    @Test
    @DisplayName("tells the matches when an administrator closed the account")
    void deleteUserByIdTellsTheMatchesTheAdministratorClosedIt() {
      User admin = TestFixtures.user(1L, UserRole.ROLE_ADMIN);
      User deletedUser = TestFixtures.user(2L);
      User matchedUser = TestFixtures.user(3L);
      authenticate(admin);
      when(userRepository.findById(2L)).thenReturn(Optional.of(deletedUser));
      when(userRepository.getAllUserMatches(2L)).thenReturn(List.of(matchedUser));

      userService.deleteUserById(2L);

      verify(notificationRepository).saveAll(notificationsCaptor.capture());
      assertThat(notificationsCaptor.getValue()).singleElement()
        .extracting(Notification::getContent)
        .isEqualTo("Admin has deleted nickname2's account");
      verify(userRepository).deleteUserByEmail("user2@pickme.com");
      verify(deletedAccountService).addDeletedAccount(deletedUser, admin);
    }
  }
}
