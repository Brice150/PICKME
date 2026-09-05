package com.packages.backend.service.impl;

import com.packages.backend.exception.UserNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.dto.UserDTOMapperRestricted;
import com.packages.backend.model.dto.UserUpdateRequest;
import com.packages.backend.model.entity.GenderAge;
import com.packages.backend.model.entity.Geolocation;
import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.Preferences;
import com.packages.backend.model.entity.Stats;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.repository.NotificationRepository;
import com.packages.backend.repository.PictureRepository;
import com.packages.backend.repository.UserRepository;
import com.packages.backend.service.DeletedAccountService;
import com.packages.backend.service.DistanceService;
import com.packages.backend.service.ServiceStatus;
import com.packages.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

  private static final String USER_EMAIL_NOT_FOUND_MSG = "user with email %s not found";
  private static final String EMAIL_ALREADY_TAKEN = "Email already taken";
  // Number of candidates returned by a single call to the selection screen.
  private static final int SELECTION_PAGE_SIZE = 25;
  // Width in kilometres of a distance group: inside a group candidates are ranked by affinity.
  private static final int DISTANCE_GROUP_WIDTH_KM = 10;
  // Number of preferences compared to compute the affinity score between two profiles.
  private static final int SCORED_ATTRIBUTES = 8;

  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final LikeRepository likeRepository;
  private final PictureRepository pictureRepository;
  private final NotificationRepository notificationRepository;
  private final UserDTOMapper userDTOMapper;
  private final UserDTOMapperRestricted userDTOMapperRestricted;
  private final PasswordEncoder passwordEncoder;
  private final DistanceService distanceService;
  private final DeletedAccountService deletedAccountService;

  public UserServiceImpl(UserRepository userRepository, MessageRepository messageRepository, LikeRepository likeRepository, PictureRepository pictureRepository, NotificationRepository notificationRepository, PasswordEncoder passwordEncoder, UserDTOMapper userDTOMapper, UserDTOMapperRestricted userDTOMapperRestricted, DistanceService distanceService, DeletedAccountService deletedAccountService) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
    this.likeRepository = likeRepository;
    this.pictureRepository = pictureRepository;
    this.notificationRepository = notificationRepository;
    this.passwordEncoder = passwordEncoder;
    this.userDTOMapper = userDTOMapper;
    this.userDTOMapperRestricted = userDTOMapperRestricted;
    this.distanceService = distanceService;
    this.deletedAccountService = deletedAccountService;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.getUserByEmail(email)
      .orElseThrow(() ->
        new UsernameNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
  }

  @Override
  @Transactional
  public String signUpUser(User user) {
    if (userRepository.getUserByEmail(user.getEmail()).isPresent()) {
      return EMAIL_ALREADY_TAKEN;
    }
    registerUser(user);
    return ServiceStatus.OK;
  }

  /**
   * Encodes the password, initialises the sub entities of the account and persists it.
   *
   * @param user validated user to persist
   */
  private void registerUser(User user) {
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
    user.setRegisteredDate(new Date());
    user.getGenderAge().setFkUser(user);
    user.getGeolocation().setFkUser(user);
    user.setPreferences(new Preferences());
    user.getPreferences().setFkUser(user);
    user.setStats(new Stats(0L, 0L, 0L, user));
    userRepository.save(user);
  }

  @Override
  public List<UserDTO> getAllSelectedUsers(Integer page) {
    User connectedUser = getConnectedUser();
    int pageNumber = page == null || page < 0 ? 0 : page;
    List<User> candidates = userRepository.getAllUsers(
      connectedUser.getGenderAge().getGenderSearch(),
      connectedUser.getGenderAge().getGender(),
      earliestBirthDateFor(connectedUser.getGenderAge().getMaxAge()),
      latestBirthDateFor(connectedUser.getGenderAge().getMinAge()),
      connectedUser.getId()
    );
    Set<Long> goldUserIds = new HashSet<>(likeRepository.getGoldByConnectedUserId(connectedUser.getId()));
    Map<Long, Double> scoreByUserId = new HashMap<>();
    candidates.forEach(candidate -> {
      candidate.setGold(goldUserIds.contains(candidate.getId()));
      candidate.getGeolocation().setDistance(distanceService.calculateDistance(connectedUser, candidate).longValue());
      scoreByUserId.put(candidate.getId(), calculateScore(connectedUser, candidate));
    });
    List<User> selectedPage = candidates.stream()
      .filter(candidate -> candidate.getGeolocation().getDistance() <= connectedUser.getGeolocation().getDistanceSearch())
      .sorted(getUserComparator(connectedUser, scoreByUserId))
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

  @Override
  public List<Match> getAllUserMatches() {
    User connectedUser = getConnectedUser();
    return userRepository.getAllUserMatches(connectedUser.getId()).stream()
      .map(user -> {
        user.getGeolocation().setDistance(distanceService.calculateDistance(connectedUser, user).longValue());
        return userDTOMapperRestricted.apply(user);
      })
      .map(user -> new Match(user, messageRepository.getUserMessagesByFk(connectedUser.getId(), user.id())))
      .toList();
  }


  @Override
  @Transactional
  public UserDTO updateUser(UserUpdateRequest request) {
    User connectedUser = getConnectedUser();
    if (null == request) {
      return userDTOMapper.apply(connectedUser);
    }
    updateMainInfos(connectedUser, request);
    updateGenderAge(connectedUser, request);
    updatePreferences(connectedUser, request);
    updateGeolocation(connectedUser, request);
    updatePassword(connectedUser, request);
    return userDTOMapper.apply(userRepository.save(connectedUser));
  }

  /**
   * Re-encodes the password when a new one has been submitted.
   *
   * @param connectedUser account to update
   * @param request       fields submitted by the owner of the account
   */
  private void updatePassword(User connectedUser, UserUpdateRequest request) {
    if (request.getPassword() != null) {
      connectedUser.setPassword(passwordEncoder.encode(request.getPassword()));
    }
  }

  /**
   * Applies the submitted geolocation, keeping the previous value of every missing field.
   *
   * @param connectedUser account to update
   * @param request       fields submitted by the owner of the account
   */
  private void updateGeolocation(User connectedUser, UserUpdateRequest request) {
    Geolocation submitted = request.getGeolocation();
    if (submitted == null) {
      return;
    }
    Geolocation current = connectedUser.getGeolocation();
    current.setLatitude(submitted.getLatitude() != null ? submitted.getLatitude() : current.getLatitude());
    current.setLongitude(submitted.getLongitude() != null ? submitted.getLongitude() : current.getLongitude());
    current.setDistanceSearch(submitted.getDistanceSearch() != null ? submitted.getDistanceSearch() : current.getDistanceSearch());
  }

  /**
   * Applies the submitted preferences, keeping the previous value of every missing field.
   *
   * @param connectedUser account to update
   * @param request       fields submitted by the owner of the account
   */
  private void updatePreferences(User connectedUser, UserUpdateRequest request) {
    Preferences submitted = request.getPreferences();
    if (submitted == null) {
      return;
    }
    Preferences current = connectedUser.getPreferences();
    current.setAlcoholDrinking(submitted.getAlcoholDrinking() != null ? submitted.getAlcoholDrinking() : current.getAlcoholDrinking());
    current.setSmokes(submitted.getSmokes() != null ? submitted.getSmokes() : current.getSmokes());
    current.setOrganised(submitted.getOrganised() != null ? submitted.getOrganised() : current.getOrganised());
    current.setPersonality(submitted.getPersonality() != null ? submitted.getPersonality() : current.getPersonality());
    current.setSportPractice(submitted.getSportPractice() != null ? submitted.getSportPractice() : current.getSportPractice());
    current.setAnimals(submitted.getAnimals() != null ? submitted.getAnimals() : current.getAnimals());
    current.setParenthood(submitted.getParenthood() != null ? submitted.getParenthood() : current.getParenthood());
    current.setGamer(submitted.getGamer() != null ? submitted.getGamer() : current.getGamer());
  }

  /**
   * Applies the submitted gender and age criteria, keeping the previous value of every missing
   * field.
   *
   * @param connectedUser account to update
   * @param request       fields submitted by the owner of the account
   */
  private void updateGenderAge(User connectedUser, UserUpdateRequest request) {
    GenderAge submitted = request.getGenderAge();
    if (submitted == null) {
      return;
    }
    GenderAge current = connectedUser.getGenderAge();
    current.setGender(submitted.getGender() != null ? submitted.getGender() : current.getGender());
    current.setGenderSearch(submitted.getGenderSearch() != null ? submitted.getGenderSearch() : current.getGenderSearch());
    current.setMinAge(submitted.getMinAge() != null ? submitted.getMinAge() : current.getMinAge());
    current.setMaxAge(submitted.getMaxAge() != null ? submitted.getMaxAge() : current.getMaxAge());
  }

  /**
   * Applies the submitted identity fields that actually changed.
   *
   * @param connectedUser account to update
   * @param request       fields submitted by the owner of the account
   */
  private void updateMainInfos(User connectedUser, UserUpdateRequest request) {
    if (request.getNickname() != null && !Objects.equals(connectedUser.getNickname(), request.getNickname())) {
      connectedUser.setNickname(request.getNickname());
    }
    if (request.getJob() != null && !Objects.equals(connectedUser.getJob(), request.getJob())) {
      connectedUser.setJob(request.getJob());
    }
    if (request.getHeight() != null && !Objects.equals(connectedUser.getHeight(), request.getHeight())) {
      connectedUser.setHeight(request.getHeight());
    }
    if (request.getDescription() != null && !Objects.equals(connectedUser.getDescription(), request.getDescription())) {
      connectedUser.setDescription(request.getDescription());
    }
  }


  @Override
  public User getUserById(Long userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new UserNotFoundException("User by id " + userId + " was not found"));
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.getUserByEmail(email)
      .orElseThrow(() -> new UserNotFoundException("User by email " + email + " was not found"));
  }

  @Override
  public User getConnectedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    return getUserByEmail(currentUserEmail);
  }

  @Override
  public UserDTO getConnectedUserDTO() {
    return userDTOMapper.apply(getConnectedUser());
  }

  @Override
  @Transactional
  public void deleteConnectedUser() {
    User connectedUser = getConnectedUser();
    deleteUser(connectedUser, connectedUser);
  }

  @Override
  @Transactional
  public void deleteUserById(Long userId) {
    User connectedUser = getConnectedUser();
    User selectedUser = getUserById(userId);
    deleteUser(selectedUser, connectedUser);
  }

  /**
   * Removes every row owned by an account then archives it. The transaction is opened by the
   * public entry points, because Spring cannot proxy a call made from inside the class.
   *
   * @param user          account to delete
   * @param connectedUser author of the deletion, the owner of the account or an administrator
   */
  private void deleteUser(User user, User connectedUser) {
    sendNotificationToMatches(user, connectedUser);
    userRepository.deleteUserNotificationsByFk(user.getId());
    userRepository.deleteUserGeolocationByFk(user.getId());
    userRepository.deleteUserPreferencesByFk(user.getId());
    userRepository.deleteUserGenderAgeByFk(user.getId());
    userRepository.deleteUserStatsByFk(user.getId());
    userRepository.deleteUserPicturesByFk(user.getId());
    userRepository.deleteUserLikesByFk(user.getId());
    userRepository.deleteUserDislikesByFk(user.getId());
    userRepository.deleteUserMessagesByFk(user.getId());
    userRepository.deleteUserByEmail(user.getEmail());
    deletedAccountService.addDeletedAccount(user, connectedUser);
  }

  /**
   * Warns every match of a deleted account that the conversation is over.
   *
   * @param user          deleted account
   * @param connectedUser author of the deletion, the owner of the account or an administrator
   */
  private void sendNotificationToMatches(User user, User connectedUser) {
    List<User> matchedUsers = userRepository.getAllUserMatches(user.getId());
    String content = connectedUser.getUserRole() == UserRole.ROLE_USER ? user.getNickname() + " has deleted his account" : "Admin has deleted " + user.getNickname() + "'s account";
    List<Notification> notifications = matchedUsers.stream()
      .map(match -> new Notification(content, "delete", new Date(), false, match))
      .toList();
    notificationRepository.saveAll(notifications);
  }

  /**
   * Computes the average gap between the preferences of two profiles: the lower the score, the
   * closer the profiles are.
   *
   * @param connectedUser connected user
   * @param user          candidate
   * @return the average gap over the compared preferences
   */
  private Double calculateScore(User connectedUser, User user) {
    int totalDifference = 0;
    totalDifference += calculateDifference(connectedUser.getPreferences().getPersonality(), user.getPreferences().getPersonality());
    totalDifference += calculateDifference(connectedUser.getPreferences().getParenthood(), user.getPreferences().getParenthood());
    totalDifference += calculateDifference(connectedUser.getPreferences().getSmokes(), user.getPreferences().getSmokes());
    totalDifference += calculateDifference(connectedUser.getPreferences().getOrganised(), user.getPreferences().getOrganised());
    totalDifference += calculateDifference(connectedUser.getPreferences().getSportPractice(), user.getPreferences().getSportPractice());
    totalDifference += calculateDifference(connectedUser.getPreferences().getAnimals(), user.getPreferences().getAnimals());
    totalDifference += calculateDifference(connectedUser.getPreferences().getAlcoholDrinking(), user.getPreferences().getAlcoholDrinking());
    totalDifference += calculateDifference(connectedUser.getPreferences().getGamer(), user.getPreferences().getGamer());
    return (double) totalDifference / SCORED_ATTRIBUTES;
  }

  /**
   * Measures the gap between two values of the same preference, a profile that did not fill in the
   * preference being penalised.
   *
   * @param connectedUserPreference preference of the connected user
   * @param userPreference          preference of the candidate
   * @return the gap between the two preferences
   */
  private int calculateDifference(Enum<?> connectedUserPreference, Enum<?> userPreference) {
    if (null == connectedUserPreference) {
      return 1;
    }
    if (null == userPreference) {
      return 2;
    }
    return Math.abs(connectedUserPreference.ordinal() - userPreference.ordinal());
  }

  /**
   * Turns the gap between two preferences into a comparison result.
   *
   * @param connectedUserPreference preference of the connected user
   * @param userPreference          preference of the candidate
   * @return a negative value when the preferences match, a positive one when they do not
   */
  private int compareAttribute(Enum<?> connectedUserPreference, Enum<?> userPreference) {
    int difference = calculateDifference(connectedUserPreference, userPreference);
    if (difference == 2) {
      return 1;
    } else if (difference == 1) {
      return 0;
    } else {
      return -1;
    }
  }

  /**
   * Builds the ranking of the candidates: closest distance group first, then best affinity score,
   * then preference by preference.
   *
   * @param connectedUser connected user
   * @param scoreByUserId affinity score of each candidate, indexed by identifier
   * @return the comparator used to sort the candidates
   */
  private Comparator<User> getUserComparator(User connectedUser, Map<Long, Double> scoreByUserId) {
    return Comparator.comparing((User user) -> getDistanceGroupIndex(user.getGeolocation().getDistance()))
      .thenComparingDouble((User user) -> scoreByUserId.get(user.getId()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getPersonality(), user.getPreferences().getPersonality()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getParenthood(), user.getPreferences().getParenthood()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getSmokes(), user.getPreferences().getSmokes()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getOrganised(), user.getPreferences().getOrganised()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getSportPractice(), user.getPreferences().getSportPractice()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getAnimals(), user.getPreferences().getAnimals()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getAlcoholDrinking(), user.getPreferences().getAlcoholDrinking()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getGamer(), user.getPreferences().getGamer()));
  }

  /**
   * Returns the distance group of a candidate, so that profiles within the same range of
   * kilometres are ranked by affinity rather than by exact distance.
   *
   * @param distance distance between the connected user and the candidate, in kilometres
   * @return the index of the distance group
   */
  private int getDistanceGroupIndex(double distance) {
    return (int) (distance / DISTANCE_GROUP_WIDTH_KM);
  }
}
