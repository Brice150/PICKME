package com.packages.backend.service;

import com.packages.backend.admin.AdminRepository;
import com.packages.backend.admin.AdminService;
import com.packages.backend.likes.Like;
import com.packages.backend.matches.Match;
import com.packages.backend.messages.Message;
import com.packages.backend.pictures.Picture;
import com.packages.backend.user.User;
import com.packages.backend.user.UserDTO;
import com.packages.backend.user.UserDTOMapper;
import com.packages.backend.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
  @Mock
  private AdminRepository adminRepository;
  @Mock
  private UserDTOMapper userDTOMapper;
  private AdminService adminService;

  @BeforeEach
  void setUp() {
    adminService = new AdminService(adminRepository, userDTOMapper);
  }

  @Test
  void findAllUsersTest() {
    // Arrange
    List<User> users = new ArrayList<>();

    User user1 = new User();
    user1.setId(1L);
    user1.setNickname("user1");
    user1.setEmail("user1@gmail.com");
    user1.setUserRole(UserRole.ROLE_ADMIN);
    user1.setPassword("password");

    User user2 = new User();
    user2.setId(2L);
    user2.setNickname("user2");
    user2.setEmail("user2@gmail.com");
    user2.setUserRole(UserRole.ROLE_USER);
    user2.setPassword("password");

    users.add(user1);
    users.add(user2);
    when(adminRepository.findAllUsers()).thenReturn(users);

    UserDTO userDTO1 = new UserDTO(
      user1.getId(),
      user1.getNickname(),
      user1.getEmail(),
      user1.getUserRole(),
      user1.getMainPicture(),
      user1.getGender(),
      user1.getGenderSearch(),
      user1.getRelationshipType(),
      user1.getBirthDate(),
      user1.getCity(),
      user1.getHeight(),
      user1.getLanguages(),
      user1.getJob(),
      user1.getDescription(),
      user1.getSmokes(),
      user1.getAlcoholDrinking(),
      user1.getOrganised(),
      user1.getPersonality(),
      user1.getSportPractice(),
      user1.getAnimals(),
      user1.getParenthood(),
      user1.getGamer(),
      user1.getActivities(),
      user1.getPictures(),
      user1.getLikes(),
      user1.getMatches(),
      user1.getMessagesSent(),
      user1.getMessagesReceived()
    );

    UserDTO userDTO2 = new UserDTO(
      user2.getId(),
      user2.getNickname(),
      user2.getEmail(),
      user2.getUserRole(),
      user2.getMainPicture(),
      user2.getGender(),
      user2.getGenderSearch(),
      user2.getRelationshipType(),
      user2.getBirthDate(),
      user2.getCity(),
      user2.getHeight(),
      user2.getLanguages(),
      user2.getJob(),
      user2.getDescription(),
      user2.getSmokes(),
      user2.getAlcoholDrinking(),
      user2.getOrganised(),
      user2.getPersonality(),
      user2.getSportPractice(),
      user2.getAnimals(),
      user2.getParenthood(),
      user2.getGamer(),
      user2.getActivities(),
      user2.getPictures(),
      user2.getLikes(),
      user2.getMatches(),
      user2.getMessagesSent(),
      user2.getMessagesReceived()
    );

    when(userDTOMapper.apply(user1)).thenReturn(userDTO1);
    when(userDTOMapper.apply(user2)).thenReturn(userDTO2);

    // Act
    List<UserDTO> userDTOs = adminService.findAllUsers();

    // Assert
    assertEquals(2, userDTOs.size());
    assertEquals(userDTO1, userDTOs.get(0));
    assertEquals(userDTO2, userDTOs.get(1));
    verify(adminRepository, times(1)).findAllUsers();
    verify(userDTOMapper, times(1)).apply(user1);
    verify(userDTOMapper, times(1)).apply(user2);
  }

  @Test
  void deleteMessageByIdTest() {
    // Arrange
    Long messageId = 1L;

    // Act
    adminService.deleteMessageById(messageId);

    // Assert
    verify(adminRepository, times(1)).deleteMessageById(messageId);
  }

  @Test
  void findUserByEmailTest() {
    // Arrange
    User user1 = new User();
    user1.setId(1L);
    user1.setNickname("user1");
    user1.setEmail("user1@gmail.com");
    user1.setUserRole(UserRole.ROLE_ADMIN);
    user1.setPassword("password");

    when(adminRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));

    // Act
    Optional<User> result = adminService.findUserByEmail(user1.getEmail());

    // Assert
    assertTrue(result.isPresent());
    assertEquals(user1.getNickname(), result.get().getNickname());
    assertEquals(user1.getEmail(), result.get().getEmail());
    assertEquals(user1.getUserRole(), result.get().getUserRole());
    verify(adminRepository, times(1)).findUserByEmail(user1.getEmail());
  }

  @Test
  void deleteUserByEmailTest() throws IOException {
    // Arrange
    User user1 = new User();
    user1.setId(1L);
    user1.setNickname("user1");
    user1.setEmail("user1@gmail.com");
    user1.setPassword("password");

    User user2 = new User();
    user2.setId(2L);
    user2.setNickname("user2");
    user2.setEmail("user2@gmail.com");
    user2.setPassword("password");

    Picture picture = new Picture(
      "picture1.jpg",
      user1
    );
    picture.setId(1L);
    Like like1 = new Like(
      new Date(), user1, user2
    );
    like1.setId(1L);
    Like like2 = new Like(
      new Date(), user2, user1
    );
    like2.setId(2L);
    Match match = new Match(
      new Date(), user1, user2
    );
    match.setId(1L);
    Message message = new Message(
      "message1", new Date(), user1, user2
    );
    message.setId(1L);

    List<Picture> pictures = List.of(picture);
    List<Like> likes = List.of(like1, like2);
    List<Match> matches = List.of(match);
    List<Message> messages = List.of(message);

    when(adminRepository.findUserByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
    when(adminRepository.findAllPicturesByFk(user1.getId())).thenReturn(pictures);
    when(adminRepository.findAllLikesByFk(user1.getId())).thenReturn(likes);
    when(adminRepository.findAllMatchesByFk(user1.getId())).thenReturn(matches);
    when(adminRepository.findAllMessagesByFk(user1.getId())).thenReturn(messages);

    // Act
    adminService.deleteUserByEmail(user1.getEmail());

    // Assert
    verify(adminRepository, times(1)).findUserByEmail(user1.getEmail());
    verify(adminRepository, times(1)).findAllPicturesByFk(user1.getId());
    verify(adminRepository, times(1)).findAllLikesByFk(user1.getId());
    verify(adminRepository, times(1)).findAllMatchesByFk(user1.getId());
    verify(adminRepository, times(1)).findAllMessagesByFk(user1.getId());
    verify(adminRepository, times(1)).deleteUserByEmail(user1.getEmail());
    verify(adminRepository, times(1)).deletePictureById(picture.getId());
    verify(adminRepository, times(1)).deleteLikeById(like1.getId());
    verify(adminRepository, times(1)).deleteMatchById(match.getId());
    verify(adminRepository, times(1)).deleteMessageById(message.getId());
  }
}
