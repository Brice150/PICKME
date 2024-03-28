package com.packages.backend.service;

import com.packages.backend.exception.UserNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.dto.UserDTOMapperRestricted;
import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.Preferences;
import com.packages.backend.model.entity.Stats;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.Gender;
import com.packages.backend.repository.LikeRepository;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserService implements UserDetailsService {

  private static final String USER_EMAIL_NOT_FOUND_MSG = "user with email %s not found";
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final LikeRepository likeRepository;
  private final UserDTOMapper userDTOMapper;
  private final UserDTOMapperRestricted userDTOMapperRestricted;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final DistanceService distanceService;

  public UserService(UserRepository userRepository, MessageRepository messageRepository, LikeRepository likeRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserDTOMapper userDTOMapper, UserDTOMapperRestricted userDTOMapperRestricted, DistanceService distanceService) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
    this.likeRepository = likeRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userDTOMapper = userDTOMapper;
    this.userDTOMapperRestricted = userDTOMapperRestricted;
    this.distanceService = distanceService;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.getUserByEmail(email)
      .orElseThrow(() ->
        new UsernameNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
  }

  public String signUpUser(User user) {
    String signUpMessage = "OK";
    String emptyPhrase = " is empty";
    if (user.getNickname() == null || user.getNickname().isBlank()) {
      signUpMessage = "Nickname" + emptyPhrase;
    } else if (user.getJob() == null || user.getJob().isBlank()) {
      signUpMessage = "Job" + emptyPhrase;
    } else if (user.getBirthDate() == null) {
      signUpMessage = "Birth date" + emptyPhrase;
    } else if (user.getEmail() == null || user.getEmail().isBlank()) {
      signUpMessage = "Email" + emptyPhrase;
    } else if (user.getPassword() == null || user.getPassword().isBlank()) {
      signUpMessage = "Password" + emptyPhrase;
    } else if (userRepository.getUserByEmail(user.getEmail()).isPresent()) {
      signUpMessage = "email already taken";
    } else if (user.getGenderAge() == null) {
      signUpMessage = "Gender or Age" + emptyPhrase;
    } else if (user.getGenderAge().getGender() == null || Gender.getDescriptionNullSafe(user.getGenderAge().getGender()).isBlank()) {
      signUpMessage = "Gender" + emptyPhrase;
    } else if (user.getGenderAge().getGenderSearch() == null || Gender.getDescriptionNullSafe(user.getGenderAge().getGenderSearch()).isBlank()) {
      signUpMessage = "Gender search" + emptyPhrase;
    } else if (user.getGenderAge().getMinAge() == null) {
      signUpMessage = "Min age" + emptyPhrase;
    } else if (user.getGenderAge().getMaxAge() == null) {
      signUpMessage = "Max age" + emptyPhrase;
    } else if (user.getGeolocation() == null) {
      signUpMessage = "Geolocation" + emptyPhrase;
    } else if (user.getGeolocation().getCity() == null || user.getGeolocation().getCity().isBlank()) {
      signUpMessage = "City" + emptyPhrase;
    } else if (user.getGeolocation().getLatitude() == null || user.getGeolocation().getLatitude().isBlank()) {
      signUpMessage = "Latitude" + emptyPhrase;
    } else if (user.getGeolocation().getLongitude() == null || user.getGeolocation().getLongitude().isBlank()) {
      signUpMessage = "Longitude" + emptyPhrase;
    } else if (user.getGeolocation().getDistanceSearch() == null) {
      signUpMessage = "Max Distance" + emptyPhrase;
    } else {
      registerUser(user);
    }
    return signUpMessage;
  }

  private void registerUser(User user) {
    String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
    user.getGenderAge().setFkUser(user);
    user.getGeolocation().setFkUser(user);
    user.setPreferences(new Preferences());
    user.getPreferences().setFkUser(user);
    user.setStats(new Stats(0L, 0L, 0L, user));
    List<Notification> notifications = new ArrayList<>();
    Notification firstNotification = new Notification("Welcome, you can start by completing your profile !", "profile", new Date(), false, user);
    notifications.add(firstNotification);
    user.setNotifications(notifications);
    userRepository.save(user);
  }

  public List<UserDTO> getAllSelectedUsers(Integer page) {
    User connectedUser = getConnectedUser();
    if (null == page) {
      page = 0;
    }
    List<User> users = userRepository.getAllUsers(connectedUser.getGenderAge().getGenderSearch(), connectedUser.getGenderAge().getGender(), connectedUser.getGenderAge().getMinAge().intValue(), connectedUser.getGenderAge().getMaxAge().intValue(), connectedUser.getId());
    List<Long> goldUserId = likeRepository.getGoldByConnectedUserId(connectedUser.getId());
    Map<Long, Double> mapAverageScoreByUserId = new HashMap<>();
    users.forEach(user -> {
      user.setGold(goldUserId.contains(user.getId()));
      mapAverageScoreByUserId.put(user.getId(), calculateScore(connectedUser, user));
      user.getGeolocation().setDistance(distanceService.calculateDistance(connectedUser, user).longValue());
    });
    return sortUsersByDistanceAndAttributes(users, connectedUser, mapAverageScoreByUserId)
      .stream().skip(page * 25L).limit(25).toList();
  }

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

  public UserDTO updateUser(User user) {
    User connectedUser = getConnectedUser();
    if (null != user && !Objects.equals(connectedUser, user)) {
      updateMainInfos(connectedUser, user);
      updateGenderAge(connectedUser, user);
      updatePreferences(connectedUser, user);
      updateGeolocation(connectedUser, user);
      updatePassword(connectedUser, user);
      return userDTOMapper.apply(userRepository.save(connectedUser));
    }
    return userDTOMapper.apply(connectedUser);
  }

  private void updatePassword(User connectedUser, User user) {
    if (user.getPassword() != null) {
      String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
      connectedUser.setPassword(encodedPassword);
    }
  }

  private void updateGeolocation(User connectedUser, User user) {
    if (user.getGeolocation() != null && !Objects.equals(connectedUser.getGeolocation(), user.getGeolocation())) {
      connectedUser.getGeolocation().setCity(user.getGeolocation().getCity() != null ? user.getGeolocation().getCity() : connectedUser.getGeolocation().getCity());
      connectedUser.getGeolocation().setLatitude(user.getGeolocation().getLatitude() != null ? user.getGeolocation().getLatitude() : connectedUser.getGeolocation().getLatitude());
      connectedUser.getGeolocation().setLongitude(user.getGeolocation().getLongitude() != null ? user.getGeolocation().getLongitude() : connectedUser.getGeolocation().getLongitude());
      connectedUser.getGeolocation().setDistanceSearch(user.getGeolocation().getDistanceSearch() != null ? user.getGeolocation().getDistanceSearch() : connectedUser.getGeolocation().getDistanceSearch());
    }
  }

  private void updatePreferences(User connectedUser, User user) {
    if (user.getPreferences() != null && !Objects.equals(connectedUser.getPreferences(), user.getPreferences())) {
      connectedUser.getPreferences().setAlcoholDrinking(user.getPreferences().getAlcoholDrinking() != null ? user.getPreferences().getAlcoholDrinking() : connectedUser.getPreferences().getAlcoholDrinking());
      connectedUser.getPreferences().setSmokes(user.getPreferences().getSmokes() != null ? user.getPreferences().getSmokes() : connectedUser.getPreferences().getSmokes());
      connectedUser.getPreferences().setOrganised(user.getPreferences().getOrganised() != null ? user.getPreferences().getOrganised() : connectedUser.getPreferences().getOrganised());
      connectedUser.getPreferences().setPersonality(user.getPreferences().getPersonality() != null ? user.getPreferences().getPersonality() : connectedUser.getPreferences().getPersonality());
      connectedUser.getPreferences().setSportPractice(user.getPreferences().getSportPractice() != null ? user.getPreferences().getSportPractice() : connectedUser.getPreferences().getSportPractice());
      connectedUser.getPreferences().setAnimals(user.getPreferences().getAnimals() != null ? user.getPreferences().getAnimals() : connectedUser.getPreferences().getAnimals());
      connectedUser.getPreferences().setParenthood(user.getPreferences().getParenthood() != null ? user.getPreferences().getParenthood() : connectedUser.getPreferences().getParenthood());
      connectedUser.getPreferences().setGamer(user.getPreferences().getGamer() != null ? user.getPreferences().getGamer() : connectedUser.getPreferences().getGamer());
    }
  }

  private void updateGenderAge(User connectedUser, User user) {
    if (user.getGenderAge() != null && !Objects.equals(connectedUser.getGenderAge(), user.getGenderAge())) {
      connectedUser.getGenderAge().setGender(user.getGenderAge().getGender() != null ? user.getGenderAge().getGender() : connectedUser.getGenderAge().getGender());
      connectedUser.getGenderAge().setGenderSearch(user.getGenderAge().getGenderSearch() != null ? user.getGenderAge().getGenderSearch() : connectedUser.getGenderAge().getGenderSearch());
      connectedUser.getGenderAge().setMinAge(user.getGenderAge().getMinAge() != null ? user.getGenderAge().getMinAge() : connectedUser.getGenderAge().getMinAge());
      connectedUser.getGenderAge().setMaxAge(user.getGenderAge().getMaxAge() != null ? user.getGenderAge().getMaxAge() : connectedUser.getGenderAge().getMaxAge());
    }
  }

  private void updateMainInfos(User connectedUser, User user) {
    if (user.getNickname() != null && !Objects.equals(connectedUser.getNickname(), user.getNickname())) {
      connectedUser.setNickname(user.getNickname());
    }
    if (user.getJob() != null && !Objects.equals(connectedUser.getJob(), user.getJob())) {
      connectedUser.setJob(user.getJob());
    }
    if (user.getHeight() != null && !Objects.equals(connectedUser.getHeight(), user.getHeight())) {
      connectedUser.setHeight(user.getHeight());
    }
    if (user.getDescription() != null && !Objects.equals(connectedUser.getDescription(), user.getDescription())) {
      connectedUser.setDescription(user.getDescription());
    }
  }

  public User getUserById(Long userId) {
    return userRepository.findById(userId)
      .orElseThrow(() -> new UserNotFoundException("User by id " + userId + " was not found"));
  }

  public User getUserByEmail(String email) {
    return userRepository.getUserByEmail(email)
      .orElseThrow(() -> new UserNotFoundException("User by email " + email + " was not found"));
  }

  public User getConnectedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    return getUserByEmail(currentUserEmail);
  }

  public UserDTO getConnectedUserDTO() {
    Optional<User> userDTO = Optional.of(getConnectedUser());
    return userDTO.map(userDTOMapper).orElse(null);
  }

  public void deleteConnectedUser() {
    User connectedUser = getConnectedUser();
    deleteUser(connectedUser);
  }

  public void deleteUserById(Long userId) {
    User selectedUser = getUserById(userId);
    deleteUser(selectedUser);
  }

  @Transactional
  private void deleteUser(User user) {
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
  }

  private Double calculateScore(User connectedUser, User user) {
    int totalDifference = 0;
    int totalAttributes = 8;

    totalDifference += calculateDifference(connectedUser.getPreferences().getPersonality(), user.getPreferences().getPersonality());
    totalDifference += calculateDifference(connectedUser.getPreferences().getParenthood(), user.getPreferences().getParenthood());
    totalDifference += calculateDifference(connectedUser.getPreferences().getSmokes(), user.getPreferences().getSmokes());
    totalDifference += calculateDifference(connectedUser.getPreferences().getOrganised(), user.getPreferences().getOrganised());
    totalDifference += calculateDifference(connectedUser.getPreferences().getSportPractice(), user.getPreferences().getSportPractice());
    totalDifference += calculateDifference(connectedUser.getPreferences().getAnimals(), user.getPreferences().getAnimals());
    totalDifference += calculateDifference(connectedUser.getPreferences().getAlcoholDrinking(), user.getPreferences().getAlcoholDrinking());
    totalDifference += calculateDifference(connectedUser.getPreferences().getGamer(), user.getPreferences().getGamer());

    return (double) totalDifference / totalAttributes;
  }

  private int calculateDifference(Enum<?> enum1, Enum<?> enum2) {
    if (null == enum1 || null == enum2) {
      return 0;
    }
    return Math.abs(enum1.ordinal() - enum2.ordinal());
  }

  private int compareAttribute(Enum<?> enum1, Enum<?> enum2) {
    int difference = calculateDifference(enum1, enum2);
    if (difference == 2) {
      return 1;
    } else if (difference == 1) {
      return 0;
    } else {
      return -1;
    }
  }

  private List<UserDTO> sortUsersByDistanceAndAttributes(List<User> users, User connectedUser, Map<Long, Double> mapAverageScoreByUserId) {
    return users.stream()
      .filter((User user) -> user.getGeolocation().getDistance() <= connectedUser.getGeolocation().getDistanceSearch())
      .sorted((getUserComparator(connectedUser, mapAverageScoreByUserId))).map(userDTOMapperRestricted).toList();
  }

  private Comparator<User> getUserComparator(User connectedUser, Map<Long, Double> mapAverageScoreByUserId) {
    return Comparator.comparing((User user) -> getDistanceGroupIndex(user.getGeolocation().getDistance()))
      .thenComparingDouble((User user) -> mapAverageScoreByUserId.get(user.getId()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getPersonality(), user.getPreferences().getPersonality()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getParenthood(), user.getPreferences().getParenthood()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getSmokes(), user.getPreferences().getSmokes()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getOrganised(), user.getPreferences().getOrganised()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getSportPractice(), user.getPreferences().getSportPractice()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getAnimals(), user.getPreferences().getAnimals()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getAlcoholDrinking(), user.getPreferences().getAlcoholDrinking()))
      .thenComparing((User user) -> compareAttribute(connectedUser.getPreferences().getGamer(), user.getPreferences().getGamer()));
  }

  private int getDistanceGroupIndex(double distance) {
    return (int) (distance / 10);
  }
}
