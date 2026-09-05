package com.packages.backend;

import com.packages.backend.model.entity.GenderAge;
import com.packages.backend.model.entity.Geolocation;
import com.packages.backend.model.entity.Preferences;
import com.packages.backend.model.entity.Stats;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.Gender;
import com.packages.backend.model.enums.UserRole;

import java.util.ArrayList;
import java.util.Date;

/**
 * Builds the entities the service tests work on. Every account is created with the sub entities
 * the services dereference, so that a test only has to override what it actually asserts on.
 */
public final class TestFixtures {

  // Paris, used as the reference position of every account.
  public static final String PARIS_LATITUDE = "48.8566";
  public static final String PARIS_LONGITUDE = "2.3522";
  // Lyon, about 392 km away from Paris.
  public static final String LYON_LATITUDE = "45.7640";
  public static final String LYON_LONGITUDE = "4.8357";

  private TestFixtures() {
  }

  /**
   * Builds a standard account located in Paris.
   *
   * @param id identifier of the account
   * @return the account
   */
  public static User user(Long id) {
    return user(id, UserRole.ROLE_USER);
  }

  /**
   * Builds an account located in Paris.
   *
   * @param id   identifier of the account
   * @param role role of the account
   * @return the account
   */
  public static User user(Long id, UserRole role) {
    User user = new User();
    user.setId(id);
    user.setUserRole(role);
    user.setNickname("nickname" + id);
    user.setEmail("user" + id + "@pickme.com");
    user.setPassword("password" + id);
    user.setJob("job" + id);
    user.setHeight(180L);
    user.setDescription("description" + id);
    user.setBirthDate(new Date(0));
    user.setRegisteredDate(new Date(0));
    user.setGenderAge(new GenderAge(Gender.MAN, Gender.WOMAN, 18L, 99L, user));
    user.setGeolocation(new Geolocation(PARIS_LATITUDE, PARIS_LONGITUDE, 100L, 0L, user));
    Preferences preferences = new Preferences();
    preferences.setFkUser(user);
    user.setPreferences(preferences);
    user.setStats(new Stats(0L, 0L, 0L, user));
    user.setPictures(new ArrayList<>());
    user.setNotifications(new ArrayList<>());
    return user;
  }
}
