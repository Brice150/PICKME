package com.packages.backend.user;

import com.packages.backend.likes.Like;
import com.packages.backend.matches.Match;
import com.packages.backend.messages.Message;
import com.packages.backend.pictures.Picture;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.nio.file.Paths.get;

@Service
public class UserService implements UserDetailsService {

  private final static String USER_EMAIL_NOT_FOUND_MSG = "user with email %s not found";
  private final UserRepository userRepository;
  private final RestrictedUserDTOMapper restrictedUserDTOMapper;
  private final UserDTOMapper userDTOMapper;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  public static final String IMAGEDIRECTORY = "src/main/resources/pictures";

  public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, RestrictedUserDTOMapper restrictedUserDTOMapper, UserDTOMapper userDTOMapper) {
    this.userRepository = userRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    this.restrictedUserDTOMapper = restrictedUserDTOMapper;
    this.userDTOMapper = userDTOMapper;
  }

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return userRepository.findUserByEmail(email)
      .orElseThrow(() ->
        new UsernameNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
  }

  public String signUpUser(User user) {
    String signUpMessage = "OK";
    String emptyPhrase = " is empty";
    if (user.getEmail() == null || user.getEmail().isBlank()) {
      signUpMessage = "Email" + emptyPhrase;
    } else if (user.getPassword() == null || user.getPassword().isBlank()) {
      signUpMessage = "Password" + emptyPhrase;
    } else if (user.getNickname() == null || user.getNickname().isBlank()) {
      signUpMessage = "Nickname" + emptyPhrase;
    } else if (user.getGender() == null || user.getGender().isBlank()) {
      signUpMessage = "Gender" + emptyPhrase;
    } else if (user.getGenderSearch() == null || user.getGenderSearch().isBlank()) {
      signUpMessage = "Gender search" + emptyPhrase;
    } else if (user.getRelationshipType() == null || user.getRelationshipType().isBlank()) {
      signUpMessage = "Relationship type" + emptyPhrase;
    } else if (user.getBirthDate() == null) {
      signUpMessage = "Birth date" + emptyPhrase;
    } else if (user.getCity() == null || user.getCity().isBlank()) {
      signUpMessage = "City" + emptyPhrase;
    } else if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
      signUpMessage = "email already taken";
    } else {
      String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
      user.setPassword(encodedPassword);

      userRepository.save(user);
    }
    return signUpMessage;
  }

  public List<RestrictedUserDTO> findAllUsers() {
    User connectedUser = findConnectedUser();
    List<User> users = userRepository.findAllUsers(connectedUser.getGenderSearch(), connectedUser.getGender(), connectedUser.getCity());
    List<Like> likes = userRepository.findAllLikesByFk(connectedUser.getId());
    users.removeIf(user -> !user.isEnabled());
    users.removeIf(user -> connectedUser.getEmail().equals(user.getEmail()));
    likes.removeIf(like -> !Objects.equals(like.getFkSender().getId(), connectedUser.getId()));
    for (Like like : likes) {
      users.removeIf(user ->
        Objects.equals(like.getFkReceiver().getId(), user.getId())
      );
    }
    Comparator<User> usersSort = Comparator
      .comparing((User user) -> compareAttributes(connectedUser.getRelationshipType(), user.getRelationshipType()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getPersonality(), user.getPersonality()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getParenthood(), user.getParenthood()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSmokes(), user.getSmokes()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getOrganised(), user.getOrganised()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSportPractice(), user.getSportPractice()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAnimals(), user.getAnimals()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAlcoholDrinking(), user.getAlcoholDrinking()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getGamer(), user.getGamer()));
    users.sort(usersSort);
    return users.stream().map(restrictedUserDTOMapper).collect(Collectors.toList());
  }

  public List<RestrictedUserDTO> findAllUsersThatLiked() {
    User connectedUser = findConnectedUser();
    List<User> users = new ArrayList<>();
    List<Like> likes = userRepository.findAllLikesByFk(connectedUser.getId());
    List<Match> matches = userRepository.findAllMatchesByFk(connectedUser.getId());
    for (Like like : likes) {
      if (Objects.equals(like.getFkReceiver().getId(), connectedUser.getId())) {
        users.add(like.getFkSender());
      }
    }
    users.removeIf(user -> !user.isEnabled());
    for (Match match : matches) {
      users.removeIf(user ->
        (Objects.equals(match.getFkReceiver().getId(), connectedUser.getId())
          && Objects.equals(match.getFkSender().getId(), user.getId()))
          || (Objects.equals(match.getFkReceiver().getId(), user.getId())
          && Objects.equals(match.getFkSender().getId(), connectedUser.getId()))
      );
    }
    Comparator<User> usersSort = Comparator
      .comparing((User user) -> compareAttributes(connectedUser.getRelationshipType(), user.getRelationshipType()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getPersonality(), user.getPersonality()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getParenthood(), user.getParenthood()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSmokes(), user.getSmokes()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getOrganised(), user.getOrganised()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getSportPractice(), user.getSportPractice()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAnimals(), user.getAnimals()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getAlcoholDrinking(), user.getAlcoholDrinking()))
      .thenComparing((User user) -> compareAttributes(connectedUser.getGamer(), user.getGamer()));
    users.sort(usersSort);
    return users.stream().map(restrictedUserDTOMapper).collect(Collectors.toList());
  }

  public List<RestrictedUserDTO> findAllUsersThatMatched() {
    User connectedUser = findConnectedUser();
    List<User> users = new ArrayList<>();
    List<Match> matches = userRepository.findAllMatchesByFk(connectedUser.getId());
    matches.sort((Match match1, Match match2) ->
      match2.getDate().compareTo(match1.getDate()));
    for (Match match : matches) {
      if (Objects.equals(match.getFkSender().getId(), connectedUser.getId())) {
        users.add(match.getFkReceiver());
      } else {
        users.add(match.getFkSender());
      }
    }
    users.removeIf(user -> !user.isEnabled());
    return users.stream().map(restrictedUserDTOMapper).collect(Collectors.toList());
  }

  public Optional<UserDTO> updateUser(User user) {
    User connectedUser = findConnectedUser();
    if (connectedUser.getId().equals(user.getId())) {
      user.setEmail(connectedUser.getEmail());
      user.setUserRole(connectedUser.getUserRole());
      if (user.getMainPicture() != null) {
        Optional<Picture> picture = userRepository.findPictureById(Long.parseLong(user.getMainPicture()));
        picture.ifPresent(value -> user.setMainPicture(value.getContent()));
      } else {
        user.setMainPicture(connectedUser.getMainPicture());
      }
      String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
      user.setPassword(encodedPassword);
      user.setIsEnabled(true);
      return Optional.of(userRepository.save(user)).map(userDTOMapper);
    } else {
      return Optional.empty();
    }
  }

  public Optional<User> findUserById(Long id) {
    return userRepository.findById(id);
  }

  public Optional<RestrictedUserDTO> findUserByIdDTO(Long id) {
    Optional<User> user = findUserById(id);
    return user.map(restrictedUserDTOMapper);
  }

  public Optional<User> findUserByEmail(String email) {
    return userRepository.findUserByEmail(email);
  }

  public User findConnectedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    Optional<User> user = findUserByEmail(currentUserEmail);
    return user.orElse(null);
  }

  public UserDTO findConnectedUserDTO() {
    Optional<User> userDTO = Optional.of(findConnectedUser());
    return userDTO.map(userDTOMapper).orElse(null);
  }

  public void deleteConnectedUser() throws IOException {
    User connectedUser = findConnectedUser();
    List<Picture> pictures = userRepository.findAllPicturesByFk(connectedUser.getId());
    for (Picture picture : pictures) {
      if (picture.getContent() != null) {
        Path imagePath = get(IMAGEDIRECTORY).normalize().resolve(picture.getContent());
        Files.deleteIfExists(imagePath);
      }
      userRepository.deletePictureById(picture.getId());
    }
    List<Like> likes = userRepository.findAllLikesByFk(connectedUser.getId());
    for (Like like : likes) {
      userRepository.deleteLikeById(like.getId());
    }
    List<Match> matches = userRepository.findAllMatchesByFk(connectedUser.getId());
    for (Match match : matches) {
      userRepository.deleteMatchById(match.getId());
    }
    List<Message> messages = userRepository.findAllMessagesByFk(connectedUser.getId());
    for (Message message : messages) {
      userRepository.deleteMessageById(message.getId());
    }
    userRepository.deleteUserByEmail(connectedUser.getEmail());
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
