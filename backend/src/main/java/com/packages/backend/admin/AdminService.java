package com.packages.backend.admin;

import com.packages.backend.likes.Like;
import com.packages.backend.matches.Match;
import com.packages.backend.messages.Message;
import com.packages.backend.pictures.Picture;
import com.packages.backend.user.User;
import com.packages.backend.user.UserDTO;
import com.packages.backend.user.UserDTOMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Paths.get;

@Service
public class AdminService {
  private final AdminRepository adminRepository;
  private final UserDTOMapper userDTOMapper;
  public static final String IMAGEDIRECTORY = "src/main/resources/pictures";

  @Autowired
  public AdminService(AdminRepository adminRepository, UserDTOMapper userDTOMapper) {
    this.adminRepository = adminRepository;
    this.userDTOMapper = userDTOMapper;
  }

  public List<UserDTO> findAllUsers() {
    List<User> users = adminRepository.findAllUsers();
    Comparator<User> usersSort = Comparator
      .comparing(User::getUserRole, (role1, role2) -> role2.compareTo(role1))
      .thenComparing(User::getNickname);
    users.sort(usersSort);
    return users.stream().map(userDTOMapper).toList();
  }

  @Transactional
  public void deleteMessageById(Long id) {
    adminRepository.deleteMessageById(id);
  }

  public Optional<User> findUserByEmail(String email) {
    return adminRepository.findUserByEmail(email);
  }

  @Transactional
  public void deleteUserByEmail(String email) throws IOException {
    Optional<User> selectedUser = findUserByEmail(email);
    Long selectedId = 0L;
    if (selectedUser.isPresent()) {
      selectedId = selectedUser.get().getId();
    }
    List<Picture> pictures = adminRepository.findAllPicturesByFk(selectedId);
    for (Picture picture : pictures) {
      if (picture.getContent() != null) {
        Path imagePath = get(IMAGEDIRECTORY).normalize().resolve(picture.getContent());
        Files.deleteIfExists(imagePath);
      }
      adminRepository.deletePictureById(picture.getId());
    }
    List<Like> likes = adminRepository.findAllLikesByFk(selectedId);
    for (Like like : likes) {
      adminRepository.deleteLikeById(like.getId());
    }
    List<Match> matches = adminRepository.findAllMatchesByFk(selectedId);
    for (Match match : matches) {
      adminRepository.deleteMatchById(match.getId());
    }
    List<Message> messages = adminRepository.findAllMessagesByFk(selectedId);
    for (Message message : messages) {
      adminRepository.deleteMessageById(message.getId());
    }
    adminRepository.deleteUserByEmail(email);
  }
}
