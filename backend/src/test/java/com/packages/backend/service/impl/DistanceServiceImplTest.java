package com.packages.backend.service.impl;

import com.packages.backend.TestFixtures;
import com.packages.backend.model.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DistanceServiceImpl")
class DistanceServiceImplTest {

  private final DistanceServiceImpl distanceService = new DistanceServiceImpl();

  @Test
  @DisplayName("returns the great circle distance between two positions")
  void calculateDistanceReturnsTheDistanceBetweenTwoPositions() {
    User connectedUser = TestFixtures.user(1L);
    User user = TestFixtures.user(2L);
    user.getGeolocation().setLatitude(TestFixtures.LYON_LATITUDE);
    user.getGeolocation().setLongitude(TestFixtures.LYON_LONGITUDE);

    Double distance = distanceService.calculateDistance(connectedUser, user);

    // Paris to Lyon, the reference value being 392 km.
    assertThat(distance).isCloseTo(392d, org.assertj.core.data.Offset.offset(2d));
  }

  @Test
  @DisplayName("returns zero for two users sharing the same position")
  void calculateDistanceReturnsZeroForTheSamePosition() {
    User connectedUser = TestFixtures.user(1L);
    User user = TestFixtures.user(2L);

    Double distance = distanceService.calculateDistance(connectedUser, user);

    assertThat(distance).isZero();
  }
}
