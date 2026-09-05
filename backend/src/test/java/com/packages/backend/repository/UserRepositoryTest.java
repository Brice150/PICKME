package com.packages.backend.repository;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.Dislike;
import com.packages.backend.model.entity.Like;
import com.packages.backend.model.entity.Message;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.Gender;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
@DisplayName("UserRepository")
class UserRepositoryTest {

  private static final Date THIRTY_YEARS_AGO = date(LocalDate.now().minusYears(30));
  // The bounds the selection screen computes for a 25 to 35 age range.
  private static final Date EARLIEST_BIRTH_DATE = date(LocalDate.now().minusYears(36).plusDays(1));
  private static final Date LATEST_BIRTH_DATE = date(LocalDate.now().minusYears(25).plusDays(1));

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private LikeRepository likeRepository;
  @Autowired
  private DislikeRepository dislikeRepository;
  @Autowired
  private MessageRepository messageRepository;
  @Autowired
  private EntityManager entityManager;

  private User connectedUser;

  private static Date date(LocalDate day) {
    return Date.from(day.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  /**
   * Persists an account, its identifier being assigned by the database.
   *
   * @param gender       gender of the account
   * @param genderSearch gender it is looking for
   * @param birthDate    birth date of the account
   * @return the persisted account
   */
  private User persistUser(Gender gender, Gender genderSearch, Date birthDate) {
    User user = TestFixtures.user(null);
    user.setEmail("user" + System.nanoTime() + "@pickme.com");
    user.getGenderAge().setGender(gender);
    user.getGenderAge().setGenderSearch(genderSearch);
    user.setBirthDate(birthDate);
    User saved = userRepository.saveAndFlush(user);
    entityManager.clear();
    return saved;
  }

  /** Runs the selection query the way the service does, for a man looking for women aged 25 to 35. */
  private List<User> selectCandidates() {
    return userRepository.getAllUsers(
      Gender.WOMAN, Gender.MAN, EARLIEST_BIRTH_DATE, LATEST_BIRTH_DATE, connectedUser.getId());
  }

  @BeforeEach
  void setUp() {
    connectedUser = persistUser(Gender.MAN, Gender.WOMAN, THIRTY_YEARS_AGO);
  }

  @Test
  @DisplayName("finds an account from its email")
  void getUserByEmailFindsTheAccount() {
    assertThat(userRepository.getUserByEmail(connectedUser.getEmail()))
      .get()
      .extracting(User::getId)
      .isEqualTo(connectedUser.getId());
    assertThat(userRepository.getUserByEmail("ghost@pickme.com")).isEmpty();
  }

  @Test
  @DisplayName("selects the candidates whose search matches the connected user in both directions")
  void getAllUsersSelectsReciprocalCandidates() {
    User match = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    User looksForWomen = persistUser(Gender.WOMAN, Gender.WOMAN, THIRTY_YEARS_AGO);
    User isAMan = persistUser(Gender.MAN, Gender.MAN, THIRTY_YEARS_AGO);

    List<User> candidates = selectCandidates();

    assertThat(candidates).extracting(User::getId).containsExactly(match.getId());
    assertThat(candidates).extracting(User::getId)
      .doesNotContain(looksForWomen.getId(), isAMan.getId());
  }

  @Test
  @DisplayName("keeps the candidates inside the requested range of birth dates")
  void getAllUsersFiltersOnTheBirthDateRange() {
    User youngEnough = persistUser(Gender.WOMAN, Gender.MAN, date(LocalDate.now().minusYears(25)));
    User oldEnough = persistUser(Gender.WOMAN, Gender.MAN, date(LocalDate.now().minusYears(35)));
    User tooYoung = persistUser(Gender.WOMAN, Gender.MAN, date(LocalDate.now().minusYears(20)));
    User tooOld = persistUser(Gender.WOMAN, Gender.MAN, date(LocalDate.now().minusYears(40)));

    List<User> candidates = selectCandidates();

    assertThat(candidates).extracting(User::getId)
      .containsExactlyInAnyOrder(youngEnough.getId(), oldEnough.getId())
      .doesNotContain(tooYoung.getId(), tooOld.getId());
  }

  @Test
  @DisplayName("never offers the connected user their own profile")
  void getAllUsersExcludesTheConnectedUser() {
    User self = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    connectedUser = self;

    assertThat(userRepository.getAllUsers(
      Gender.MAN, Gender.WOMAN, EARLIEST_BIRTH_DATE, LATEST_BIRTH_DATE, self.getId()))
      .extracting(User::getId)
      .doesNotContain(self.getId());
  }

  @Test
  @DisplayName("drops the profiles the connected user has already answered")
  void getAllUsersExcludesTheProfilesAlreadyAnswered() {
    User liked = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    User disliked = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    User untouched = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    likeRepository.saveAndFlush(new Like(new Date(), connectedUser.getId(), liked.getId()));
    dislikeRepository.saveAndFlush(new Dislike(new Date(), connectedUser.getId(), disliked.getId()));

    List<User> candidates = selectCandidates();

    assertThat(candidates).extracting(User::getId).containsExactly(untouched.getId());
  }

  @Test
  @DisplayName("still offers a profile that answered the connected user first")
  void getAllUsersKeepsTheProfilesThatAnsweredFirst() {
    User admirer = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    likeRepository.saveAndFlush(new Like(new Date(), admirer.getId(), connectedUser.getId()));

    assertThat(selectCandidates()).extracting(User::getId).containsExactly(admirer.getId());
  }

  @Test
  @DisplayName("returns the accounts that liked the connected user back")
  void getAllUserMatchesReturnsTheMutualLikes() {
    User matched = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    User oneWay = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    likeRepository.saveAndFlush(new Like(new Date(), connectedUser.getId(), matched.getId()));
    likeRepository.saveAndFlush(new Like(new Date(), matched.getId(), connectedUser.getId()));
    likeRepository.saveAndFlush(new Like(new Date(), connectedUser.getId(), oneWay.getId()));

    List<User> matches = userRepository.getAllUserMatches(connectedUser.getId());

    assertThat(matches).extracting(User::getId).containsExactly(matched.getId());
  }

  @Test
  @DisplayName("puts the conversation with the most recent activity first")
  void getAllUserMatchesOrdersOnTheLastActivity() {
    User quiet = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    User chatty = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    Date old = date(LocalDate.now().minusDays(10));
    for (User match : List.of(quiet, chatty)) {
      likeRepository.saveAndFlush(new Like(old, connectedUser.getId(), match.getId()));
      likeRepository.saveAndFlush(new Like(old, match.getId(), connectedUser.getId()));
    }
    messageRepository.saveAndFlush(
      new Message("hello", new Date(), "nickname", chatty.getId(), connectedUser.getId()));

    List<User> matches = userRepository.getAllUserMatches(connectedUser.getId());

    assertThat(matches).extracting(User::getId)
      .containsExactly(chatty.getId(), quiet.getId());
  }

  @Test
  @DisplayName("removes everything an account owns before it is archived")
  void theDeleteQueriesEmptyTheAccount() {
    User other = persistUser(Gender.WOMAN, Gender.MAN, THIRTY_YEARS_AGO);
    likeRepository.saveAndFlush(new Like(new Date(), connectedUser.getId(), other.getId()));
    dislikeRepository.saveAndFlush(new Dislike(new Date(), connectedUser.getId(), other.getId()));
    messageRepository.saveAndFlush(
      new Message("hello", new Date(), "nickname", connectedUser.getId(), other.getId()));

    Long id = connectedUser.getId();
    userRepository.deleteUserNotificationsByFk(id);
    userRepository.deleteUserGeolocationByFk(id);
    userRepository.deleteUserPreferencesByFk(id);
    userRepository.deleteUserGenderAgeByFk(id);
    userRepository.deleteUserStatsByFk(id);
    userRepository.deleteUserPicturesByFk(id);
    userRepository.deleteUserLikesByFk(id);
    userRepository.deleteUserDislikesByFk(id);
    userRepository.deleteUserMessagesByFk(id);
    userRepository.deleteUserByEmail(connectedUser.getEmail());
    entityManager.flush();
    entityManager.clear();

    assertThat(userRepository.findById(id)).isEmpty();
    assertThat(likeRepository.getLikeByFk(id, other.getId())).isEmpty();
    assertThat(dislikeRepository.getDislikeByFk(id, other.getId())).isEmpty();
    assertThat(messageRepository.getUserMessagesByFk(id, other.getId())).isEmpty();
  }
}
