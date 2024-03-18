package com.packages.backend.service;

import com.packages.backend.exception.UserNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserDTO;
import com.packages.backend.model.user.UserDTOMapper;
import com.packages.backend.model.user.UserDTOMapperRestricted;
import com.packages.backend.model.user.enums.*;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

  private static final String USER_EMAIL_NOT_FOUND_MSG = "user with email %s not found";
  private final UserRepository userRepository;
  private final MessageRepository messageRepository;
  private final LikeRepository likeRepository;
  private final UserDTOMapper userDTOMapper;
  private final UserDTOMapperRestricted userDTOMapperRestricted;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public UserService(UserRepository userRepository, MessageRepository messageRepository, LikeRepository likeRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserDTOMapper userDTOMapper, UserDTOMapperRestricted userDTOMapperRestricted) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
    this.likeRepository = likeRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userDTOMapper = userDTOMapper;
    this.userDTOMapperRestricted = userDTOMapperRestricted;
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
    } else if (user.getCity() == null || user.getCity().isBlank()) {
      signUpMessage = "City" + emptyPhrase;
    } else if (user.getBirthDate() == null) {
      signUpMessage = "Birth date" + emptyPhrase;
    } else if (user.getGender() == null || Gender.getDescriptionNullSafe(user.getGender()).isBlank()) {
      signUpMessage = "Gender" + emptyPhrase;
    } else if (user.getGenderSearch() == null || Gender.getDescriptionNullSafe(user.getGenderSearch()).isBlank()) {
      signUpMessage = "Gender search" + emptyPhrase;
    } else if (user.getMinAge() == null) {
      signUpMessage = "Min age" + emptyPhrase;
    } else if (user.getMaxAge() == null) {
      signUpMessage = "Max age" + emptyPhrase;
    } else if (user.getEmail() == null || user.getEmail().isBlank()) {
      signUpMessage = "Email" + emptyPhrase;
    } else if (user.getPassword() == null || user.getPassword().isBlank()) {
      signUpMessage = "Password" + emptyPhrase;
    } else if (userRepository.getUserByEmail(user.getEmail()).isPresent()) {
      signUpMessage = "email already taken";
    } else {
      String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
      user.setPassword(encodedPassword);

      userRepository.save(user);
    }
    return signUpMessage;
  }

  public List<UserDTO> getAllSelectedUsers() {
    User connectedUser = getConnectedUser();
    List<User> users = userRepository.getAllUsers(connectedUser.getGenderSearch(), connectedUser.getGender(), connectedUser.getMinAge().intValue(), connectedUser.getMaxAge().intValue(), connectedUser.getId());
    List<Long> goldUserId = likeRepository.getGoldByConnectedUserId(connectedUser.getId());
    users.forEach(user -> user.setGold(goldUserId.contains(user.getId())));
    Comparator<User> usersSort = Comparator
      .comparing((User user) -> compareAttributes(connectedUser.getCity(), user.getCity()))
      .thenComparing((User user) -> compareAttributes(Personality.getDescriptionNullSafe(connectedUser.getPersonality()), Personality.getDescriptionNullSafe(user.getPersonality())))
      .thenComparing((User user) -> compareAttributes(Parenthood.getDescriptionNullSafe(connectedUser.getParenthood()), Parenthood.getDescriptionNullSafe(user.getParenthood())))
      .thenComparing((User user) -> compareAttributes(Smokes.getDescriptionNullSafe(connectedUser.getSmokes()), Smokes.getDescriptionNullSafe(user.getSmokes())))
      .thenComparing((User user) -> compareAttributes(Organised.getDescriptionNullSafe(connectedUser.getOrganised()), Organised.getDescriptionNullSafe(user.getOrganised())))
      .thenComparing((User user) -> compareAttributes(SportPractice.getDescriptionNullSafe(connectedUser.getSportPractice()), SportPractice.getDescriptionNullSafe(user.getSportPractice())))
      .thenComparing((User user) -> compareAttributes(Animals.getDescriptionNullSafe(connectedUser.getAnimals()), Animals.getDescriptionNullSafe(user.getAnimals())))
      .thenComparing((User user) -> compareAttributes(AlcoholDrinking.getDescriptionNullSafe(connectedUser.getAlcoholDrinking()), AlcoholDrinking.getDescriptionNullSafe(user.getAlcoholDrinking())))
      .thenComparing((User user) -> compareAttributes(Gamer.getDescriptionNullSafe(connectedUser.getGamer()), Gamer.getDescriptionNullSafe(user.getGamer())));
    users.sort(usersSort);
    return users.stream().map(userDTOMapperRestricted).toList();
  }

  public List<Match> getAllUserMatches() {
    User connectedUser = getConnectedUser();
    List<UserDTO> users = userRepository.getAllUserMatches(connectedUser.getId()).stream().map(userDTOMapperRestricted).toList();
    return users.stream()
      .map(user -> new Match(user, messageRepository.getUserMessagesByFk(connectedUser.getId(), user.id()))).toList();
  }

  public Optional<UserDTO> updateUser(User user) {
    User connectedUser = getConnectedUser();
    if (connectedUser.getId().equals(user.getId())) {
      connectedUser.setMainPicture(user.getMainPicture() != null ? user.getMainPicture() : connectedUser.getMainPicture());
      connectedUser.setNickname(user.getNickname() != null ? user.getNickname() : connectedUser.getNickname());
      connectedUser.setJob(user.getJob() != null ? user.getJob() : connectedUser.getJob());
      connectedUser.setCity(user.getCity() != null ? user.getCity() : connectedUser.getCity());
      connectedUser.setHeight(user.getHeight() != null ? user.getHeight() : connectedUser.getHeight());
      connectedUser.setGender(user.getGender() != null ? user.getGender() : connectedUser.getGender());
      connectedUser.setGenderSearch(user.getGenderSearch() != null ? user.getGenderSearch() : connectedUser.getGenderSearch());
      connectedUser.setMinAge(user.getMinAge() != null ? user.getMinAge() : connectedUser.getMinAge());
      connectedUser.setMaxAge(user.getMaxAge() != null ? user.getMaxAge() : connectedUser.getMaxAge());
      if (user.getPassword() != null) {
        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        connectedUser.setPassword(encodedPassword);
      }
      connectedUser.setDescription(user.getDescription() != null ? user.getDescription() : connectedUser.getDescription());
      connectedUser.setAlcoholDrinking(user.getAlcoholDrinking() != null ? user.getAlcoholDrinking() : connectedUser.getAlcoholDrinking());
      connectedUser.setSmokes(user.getSmokes() != null ? user.getSmokes() : connectedUser.getSmokes());
      connectedUser.setOrganised(user.getOrganised() != null ? user.getOrganised() : connectedUser.getOrganised());
      connectedUser.setPersonality(user.getPersonality() != null ? user.getPersonality() : connectedUser.getPersonality());
      connectedUser.setSportPractice(user.getSportPractice() != null ? user.getSportPractice() : connectedUser.getSportPractice());
      connectedUser.setAnimals(user.getAnimals() != null ? user.getAnimals() : connectedUser.getAnimals());
      connectedUser.setParenthood(user.getParenthood() != null ? user.getParenthood() : connectedUser.getParenthood());
      connectedUser.setGamer(user.getGamer() != null ? user.getGamer() : connectedUser.getGamer());
      return Optional.of(userRepository.save(connectedUser)).map(userDTOMapper);
    } else {
      return Optional.empty();
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

  @Transactional
  public void deleteConnectedUser() {
    User connectedUser = getConnectedUser();
    userRepository.deleteUserPicturesByFk(connectedUser.getId());
    userRepository.deleteUserLikesByFk(connectedUser.getId());
    userRepository.deleteUserDislikesByFk(connectedUser.getId());
    userRepository.deleteUserMessagesByFk(connectedUser.getId());
    userRepository.deleteUserByEmail(connectedUser.getEmail());
  }

  @Transactional
  public void deleteUserById(Long userId) {
    User selectedUser = getUserById(userId);
    userRepository.deleteUserPicturesByFk(selectedUser.getId());
    userRepository.deleteUserLikesByFk(selectedUser.getId());
    userRepository.deleteUserDislikesByFk(selectedUser.getId());
    userRepository.deleteUserMessagesByFk(selectedUser.getId());
    userRepository.deleteUserByEmail(selectedUser.getEmail());
  }

  public static int compareAttributes(String attribute1, String attribute2) {
    if ((attribute1 == null || attribute2 == null)) {
      return 0;
    } else {
      if (attribute1.equals(attribute2)) {
        return -1;
      } else {
        return 1;
      }
    }
  }
}
