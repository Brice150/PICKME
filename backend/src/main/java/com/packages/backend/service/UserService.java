package com.packages.backend.service;

import com.packages.backend.exception.UserNotFoundException;
import com.packages.backend.model.Match;
import com.packages.backend.model.user.User;
import com.packages.backend.model.user.UserDTO;
import com.packages.backend.model.user.UserDTOMapper;
import com.packages.backend.model.user.UserDTOMapperHiddenRole;
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
  private final UserDTOMapper userDTOMapper;
  private final UserDTOMapperHiddenRole userDTOMapperHiddenRole;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public UserService(UserRepository userRepository, MessageRepository messageRepository, BCryptPasswordEncoder bCryptPasswordEncoder, UserDTOMapper userDTOMapper, UserDTOMapperHiddenRole userDTOMapperHiddenRole) {
    this.userRepository = userRepository;
    this.messageRepository = messageRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.userDTOMapper = userDTOMapper;
    this.userDTOMapperHiddenRole = userDTOMapperHiddenRole;
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
    } else if (user.getGender() == null || user.getGender().isBlank()) {
      signUpMessage = "Gender" + emptyPhrase;
    } else if (user.getGenderSearch() == null || user.getGenderSearch().isBlank()) {
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
    Comparator<User> usersSort = Comparator
      .comparing((User user) -> compareAttributes(connectedUser.getCity(), user.getCity()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getPersonality(), user.getPersonality()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getParenthood(), user.getParenthood()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSmokes(), user.getSmokes()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getOrganised(), user.getOrganised()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSportPractice(), user.getSportPractice()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAnimals(), user.getAnimals()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAlcoholDrinking(), user.getAlcoholDrinking()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getGamer(), user.getGamer()));
    users.sort(usersSort);
    return users.stream().map(userDTOMapperHiddenRole).toList();
  }

  public List<Match> getAllUserMatches() {
    User connectedUser = getConnectedUser();
    List<UserDTO> users = userRepository.getAllUserMatches(connectedUser.getId()).stream().map(userDTOMapperHiddenRole).toList();
    return users.stream()
      .map(user -> new Match(user, messageRepository.getUserMessagesByFk(connectedUser.getId(), user.id()))).toList();
  }

  public Optional<UserDTO> updateUser(User user) {
    User connectedUser = getConnectedUser();
    if (connectedUser.getId().equals(user.getId())) {
      user.setEmail(connectedUser.getEmail());
      user.setUserRole(connectedUser.getUserRole());
      String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
      user.setPassword(encodedPassword);
      user.setIsEnabled(true);
      return Optional.of(userRepository.save(user)).map(userDTOMapper);
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
