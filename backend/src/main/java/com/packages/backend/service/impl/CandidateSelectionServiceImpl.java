package com.packages.backend.service.impl;

import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapperRestricted;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.PictureRepository;
import com.packages.backend.repository.UserRepository;
import com.packages.backend.service.CandidateSelectionService;
import com.packages.backend.service.DistanceService;
import com.packages.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CandidateSelectionServiceImpl implements CandidateSelectionService {

  // Number of candidates returned by a single call to the selection screen.
  private static final int SELECTION_PAGE_SIZE = 25;

  private final UserRepository userRepository;
  private final LikeRepository likeRepository;
  private final PictureRepository pictureRepository;
  private final UserDTOMapperRestricted userDTOMapperRestricted;
  private final UserService userService;
  private final DistanceService distanceService;
  private final AffinityScorer affinityScorer;

  public CandidateSelectionServiceImpl(UserRepository userRepository, LikeRepository likeRepository, PictureRepository pictureRepository, UserDTOMapperRestricted userDTOMapperRestricted, UserService userService, DistanceService distanceService, AffinityScorer affinityScorer) {
    this.userRepository = userRepository;
    this.likeRepository = likeRepository;
    this.pictureRepository = pictureRepository;
    this.userDTOMapperRestricted = userDTOMapperRestricted;
    this.userService = userService;
    this.distanceService = distanceService;
    this.affinityScorer = affinityScorer;
  }

  @Override
  public List<UserDTO> getAllSelectedUsers(Integer page) {
    User connectedUser = userService.getConnectedUser();
    int pageNumber = page == null || page < 0 ? 0 : page;
    // The candidates of the gender the connected user is looking for, looking for their own.
    List<User> candidates = userRepository.getAllUsers(
      connectedUser.getGenderAge().getGenderSearch(),
      connectedUser.getGenderAge().getGender(),
      earliestBirthDateFor(connectedUser.getGenderAge().getMaxAge()),
      latestBirthDateFor(connectedUser.getGenderAge().getMinAge()),
      connectedUser.getId()
    );
    // The profiles that liked the connected user first are highlighted on the card.
    Set<Long> goldUserIds = new HashSet<>(likeRepository.getGoldByConnectedUserId(connectedUser.getId()));
    Map<Long, Double> scoreByUserId = new HashMap<>();
    candidates.forEach(candidate -> {
      candidate.setGold(goldUserIds.contains(candidate.getId()));
      candidate.getGeolocation().setDistance(distanceService.calculateDistance(connectedUser, candidate).longValue());
      scoreByUserId.put(candidate.getId(), affinityScorer.score(connectedUser, candidate));
    });
    List<User> selectedPage = candidates.stream()
      .filter(candidate -> candidate.getGeolocation().getDistance() <= connectedUser.getGeolocation().getDistanceSearch())
      .sorted(affinityScorer.ranking(connectedUser, scoreByUserId))
      .skip((long) pageNumber * SELECTION_PAGE_SIZE)
      .limit(SELECTION_PAGE_SIZE)
      .toList();
    return toRestrictedViewsWithMainPicture(selectedPage);
  }

  /**
   * Returns the first birth date of a candidate that is not older than the requested age, the day
   * their next birthday makes them cross that age.
   *
   * @param maxAge oldest age accepted by the connected user
   * @return the lower bound, included, of the birth dates to select
   */
  private Date earliestBirthDateFor(Long maxAge) {
    return toDate(LocalDate.now().minusYears(maxAge + 1L).plusDays(1));
  }

  /**
   * Returns the first birth date of a candidate that is too young to be selected, so that the
   * whole day of the birthday of the youngest accepted candidates stays included.
   *
   * @param minAge youngest age accepted by the connected user
   * @return the upper bound, excluded, of the birth dates to select
   */
  private Date latestBirthDateFor(Long minAge) {
    return toDate(LocalDate.now().minusYears(minAge).plusDays(1));
  }

  private Date toDate(LocalDate date) {
    return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
  }

  /**
   * Maps the candidates of the current page, attaching to each of them its main picture only. The
   * pictures of the whole page are read in a single query instead of letting the JSON
   * serialization trigger one lazy loading per candidate.
   *
   * @param users candidates of the current page
   * @return the restricted views of the candidates
   */
  private List<UserDTO> toRestrictedViewsWithMainPicture(List<User> users) {
    if (users.isEmpty()) {
      return List.of();
    }
    List<Long> userIds = users.stream().map(User::getId).toList();
    Map<Long, List<Picture>> pictureByUserId = pictureRepository.findDisplayedPicturesByUserIds(userIds).stream()
      .collect(Collectors.groupingBy(picture -> picture.getFkUser().getId()));
    return users.stream()
      .map(user -> userDTOMapperRestricted.apply(user, pictureByUserId.getOrDefault(user.getId(), List.of())))
      .toList();
  }
}
