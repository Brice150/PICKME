package com.packages.backend.service.impl;

import com.packages.backend.exception.UserNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserDTOMapper;
import com.packages.backend.model.dto.UserDTOMapperRestricted;
import com.packages.backend.model.dto.UserUpdateRequest;
import com.packages.backend.model.entity.GenderAge;
import com.packages.backend.model.entity.Geolocation;
import com.packages.backend.model.entity.Preferences;
import com.packages.backend.model.entity.Stats;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.MessageRepository;
import com.packages.backend.repository.UserRepository;
import com.packages.backend.service.DistanceService;
import com.packages.backend.service.RegistrationResult;
import com.packages.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Owns the account itself: who is connected, how one is created, read and updated. The selection
 * of the candidates and the closing of an account live in their own services, which this one only
 * lends {@link #getConnectedUser()} and {@link #getUserById(Long)} to.
 */
@Service
public class UserServiceImpl implements UserService {

  private static final String USER_EMAIL_NOT_FOUND_MSG = "user with email %s not found";
  private static final String EMAIL_ALREADY_TAKEN = "Email already taken";

  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final UserDTOMapper userDTOMapper;
  private final UserDTOMapperRestricted userDTOMapperRestricted;
  private final PasswordEncoder passwordEncoder;
  private final DistanceService distanceService;

  public UserServiceImpl(UserRepository userRepository, MessageRepository messageRepository, PasswordEncoder passwordEncoder, UserDTOMapper userDTOMapper, UserDTOMapperRestricted userDTOMapperRestricted, DistanceService distanceService) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
    this.passwordEncoder = passwordEncoder;
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

  @Override
  @Transactional
  public RegistrationResult signUpUser(User user) {
    if (userRepository.getUserByEmail(user.getEmail()).isPresent()) {
      return new RegistrationResult.Rejected(EMAIL_ALREADY_TAKEN);
    }
    registerUser(user);
    return new RegistrationResult.Created();
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
}
