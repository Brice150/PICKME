package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.entity.Preferences;
import com.packages.backend.model.enums.AlcoholDrinking;
import com.packages.backend.model.enums.Animals;
import com.packages.backend.model.enums.Gamer;
import com.packages.backend.model.enums.Organised;
import com.packages.backend.model.enums.Parenthood;
import com.packages.backend.model.enums.Personality;
import com.packages.backend.model.enums.SportPractice;
import com.packages.backend.model.enums.Smokes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@DisplayName("AffinityScorer")
class AffinityScorerTest {

  private final AffinityScorer affinityScorer = new AffinityScorer();

  /** Builds a profile whose eight preferences are all answered the same way. */
  private User profile(Long id, Answer answer) {
    User user = TestFixtures.user(id);
    Preferences preferences = user.getPreferences();
    preferences.setSmokes(Smokes.valueOf(answer.name()));
    preferences.setAlcoholDrinking(AlcoholDrinking.valueOf(answer.name()));
    preferences.setOrganised(Organised.valueOf(answer.name()));
    preferences.setSportPractice(SportPractice.valueOf(answer.name()));
    preferences.setAnimals(Animals.valueOf(answer.name()));
    preferences.setParenthood(Parenthood.valueOf(answer.name()));
    preferences.setGamer(Gamer.valueOf(answer.name()));
    preferences.setPersonality(Personality.INTROVERT);
    return user;
  }

  /** The three answers every preference of the application shares. */
  private enum Answer { NO, MAYBE, YES }

  @Test
  @DisplayName("scores two identical profiles at zero")
  void identicalProfilesScoreZero() {
    User connectedUser = profile(1L, Answer.NO);
    User twin = profile(2L, Answer.NO);

    assertThat(affinityScorer.score(connectedUser, twin)).isZero();
  }

  @Test
  @DisplayName("scores the gap between the answers of two profiles")
  void differentAnswersRaiseTheScore() {
    User connectedUser = profile(1L, Answer.NO);
    User opposite = profile(2L, Answer.YES);

    // Seven preferences are two steps apart, the personality is shared.
    assertThat(affinityScorer.score(connectedUser, opposite)).isCloseTo(14d / 8, within(0.001));
  }

  @Test
  @DisplayName("penalises a candidate that left a preference empty more than the connected user")
  void anEmptyPreferenceIsPenalised() {
    User connectedUser = profile(1L, Answer.NO);
    User silentCandidate = profile(2L, Answer.NO);
    silentCandidate.getPreferences().setSmokes(null);
    User silentConnectedUser = profile(1L, Answer.NO);
    silentConnectedUser.getPreferences().setSmokes(null);

    // A candidate that did not answer costs two, the connected user that did not costs one.
    assertThat(affinityScorer.score(connectedUser, silentCandidate)).isCloseTo(2d / 8, within(0.001));
    assertThat(affinityScorer.score(silentConnectedUser, profile(2L, Answer.NO)))
      .isCloseTo(1d / 8, within(0.001));
  }

  @Test
  @DisplayName("ranks the closest distance group first")
  void theClosestDistanceGroupComesFirst() {
    User connectedUser = profile(1L, Answer.NO);
    User near = profile(2L, Answer.NO);
    User far = profile(3L, Answer.NO);
    near.getGeolocation().setDistance(5L);
    far.getGeolocation().setDistance(25L);
    Map<Long, Double> scores = Map.of(2L, 0d, 3L, 0d);

    List<User> ranked = List.of(far, near).stream()
      .sorted(affinityScorer.ranking(connectedUser, scores))
      .toList();

    assertThat(ranked).extracting(User::getId).containsExactly(2L, 3L);
  }

  @Test
  @DisplayName("ranks on the affinity score inside a distance group")
  void theBestAffinityComesFirstInsideAGroup() {
    User connectedUser = profile(1L, Answer.NO);
    User close = profile(2L, Answer.NO);
    User distant = profile(3L, Answer.NO);
    close.getGeolocation().setDistance(8L);
    distant.getGeolocation().setDistance(2L);
    Map<Long, Double> scores = Map.of(2L, 0.1, 3L, 1.5);

    List<User> ranked = List.of(distant, close).stream()
      .sorted(affinityScorer.ranking(connectedUser, scores))
      .toList();

    assertThat(ranked).extracting(User::getId).containsExactly(2L, 3L);
  }

  @Test
  @DisplayName("falls back on the preferences when distance and score are tied")
  void thePreferencesBreakTheTie() {
    User connectedUser = profile(1L, Answer.NO);
    // Same answers as the connected user, so every preference compares as a match.
    User sameAnswers = profile(2L, Answer.NO);
    // No answer at all, so every preference compares as a mismatch.
    User noAnswers = TestFixtures.user(3L);
    sameAnswers.getGeolocation().setDistance(5L);
    noAnswers.getGeolocation().setDistance(5L);
    Map<Long, Double> scores = Map.of(2L, 1d, 3L, 1d);

    List<User> ranked = List.of(noAnswers, sameAnswers).stream()
      .sorted(affinityScorer.ranking(connectedUser, scores))
      .toList();

    assertThat(ranked).extracting(User::getId).containsExactly(2L, 3L);
  }

  @Test
  @DisplayName("treats a preference the connected user left empty as neutral")
  void anEmptyPreferenceOfTheConnectedUserIsNeutral() {
    User connectedUser = TestFixtures.user(1L);
    User answered = profile(2L, Answer.NO);
    User silent = TestFixtures.user(3L);
    answered.getGeolocation().setDistance(5L);
    silent.getGeolocation().setDistance(5L);
    Map<Long, Double> scores = Map.of(2L, 1d, 3L, 1d);

    List<User> ranked = List.of(answered, silent).stream()
      .sorted(affinityScorer.ranking(connectedUser, scores))
      .toList();

    // Neither candidate wins on preferences, so the encounter order is kept.
    assertThat(ranked).extracting(User::getId).containsExactly(2L, 3L);
  }
}
