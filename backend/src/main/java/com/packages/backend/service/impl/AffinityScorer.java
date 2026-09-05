package com.packages.backend.service.impl;

import com.packages.backend.model.entity.Preferences;
import com.packages.backend.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Ranks the candidates of the selection screen. The preferences are read through a single list of
 * accessors so that scoring and ordering always compare the same criteria, in the same order.
 */
@Component
public class AffinityScorer {

  // Width in kilometres of a distance group: inside a group candidates are ranked by affinity.
  private static final int DISTANCE_GROUP_WIDTH_KM = 10;

  // The preferences compared to measure how close two profiles are.
  private static final List<Function<Preferences, Enum<?>>> SCORED_PREFERENCES = List.of(
    Preferences::getPersonality,
    Preferences::getParenthood,
    Preferences::getSmokes,
    Preferences::getOrganised,
    Preferences::getSportPractice,
    Preferences::getAnimals,
    Preferences::getAlcoholDrinking,
    Preferences::getGamer
  );

  /**
   * Computes the average gap between the preferences of two profiles: the lower the score, the
   * closer the profiles are.
   *
   * @param connectedUser connected user
   * @param candidate     candidate
   * @return the average gap over the compared preferences
   */
  public double score(User connectedUser, User candidate) {
    int totalDifference = 0;
    for (Function<Preferences, Enum<?>> preference : SCORED_PREFERENCES) {
      totalDifference += difference(
        preference.apply(connectedUser.getPreferences()),
        preference.apply(candidate.getPreferences()));
    }
    return (double) totalDifference / SCORED_PREFERENCES.size();
  }

  /**
   * Builds the ranking of the candidates: closest distance group first, then best affinity score,
   * then preference by preference.
   *
   * @param connectedUser connected user
   * @param scoreByUserId affinity score of each candidate, indexed by identifier
   * @return the comparator used to sort the candidates
   */
  public Comparator<User> ranking(User connectedUser, Map<Long, Double> scoreByUserId) {
    Comparator<User> ranking = Comparator
      .comparingInt((User user) -> distanceGroupIndex(user.getGeolocation().getDistance()))
      .thenComparingDouble(user -> scoreByUserId.get(user.getId()));
    for (Function<Preferences, Enum<?>> preference : SCORED_PREFERENCES) {
      ranking = ranking.thenComparingInt(user -> compare(
        preference.apply(connectedUser.getPreferences()),
        preference.apply(user.getPreferences())));
    }
    return ranking;
  }

  /**
   * Measures the gap between two values of the same preference, a profile that did not fill in the
   * preference being penalised.
   *
   * @param connectedUserPreference preference of the connected user
   * @param candidatePreference     preference of the candidate
   * @return the gap between the two preferences
   */
  private int difference(Enum<?> connectedUserPreference, Enum<?> candidatePreference) {
    if (null == connectedUserPreference) {
      return 1;
    }
    if (null == candidatePreference) {
      return 2;
    }
    return Math.abs(connectedUserPreference.ordinal() - candidatePreference.ordinal());
  }

  /**
   * Turns the gap between two preferences into a comparison result.
   *
   * @param connectedUserPreference preference of the connected user
   * @param candidatePreference     preference of the candidate
   * @return a negative value when the preferences match, a positive one when they do not
   */
  private int compare(Enum<?> connectedUserPreference, Enum<?> candidatePreference) {
    int difference = difference(connectedUserPreference, candidatePreference);
    if (difference == 2) {
      return 1;
    } else if (difference == 1) {
      return 0;
    } else {
      return -1;
    }
  }

  /**
   * Returns the distance group of a candidate, so that profiles within the same range of
   * kilometres are ranked by affinity rather than by exact distance.
   *
   * @param distance distance between the connected user and the candidate, in kilometres
   * @return the index of the distance group
   */
  private int distanceGroupIndex(double distance) {
    return (int) (distance / DISTANCE_GROUP_WIDTH_KM);
  }
}
