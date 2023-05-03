package com.packages.backend.admin;

import com.packages.backend.likes.Like;
import com.packages.backend.matches.Match;
import com.packages.backend.messages.Message;
import com.packages.backend.pictures.Picture;
import com.packages.backend.pictures.PictureNotFoundException;
import com.packages.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static java.nio.file.Paths.get;

@Service
public class AdminService {
  private final AdminRepository adminRepository;
  public static final String IMAGEDIRECTORY = "src/main/resources/pictures";
  private final static String USER_EMAIL_NOT_FOUND_MSG = "user with email %s not found";

  @Autowired
  public AdminService(AdminRepository adminRepository) {
    this.adminRepository = adminRepository;
  }

  public List<User> findAllUsers() {
    List<User> users = adminRepository.findAllUsers();
    for (User user : users) {
      user.setMessagesReceived(null);
      user.setPassword(null);
      user.setTokens(null);
    }
    Comparator<User> usersSort = Comparator
      .comparing(User::getUserRole, (role1, role2) -> role2.compareTo(role1))
      .thenComparing(User::getUsername);
    users.sort(usersSort);
    return users;
  }

  @Transactional
  public void deleteMessageById(Long id) {
    adminRepository.deleteMessageById(id);
  }

  public User findUserByEmail(String email) {
    return adminRepository.findUserByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_EMAIL_NOT_FOUND_MSG, email)));
  }

  @Transactional
  public void deleteUserByEmail(String email) throws IOException {
    User selectedUser = findUserByEmail(email);
    List<Picture> pictures = adminRepository.findAllPicturesByFk(selectedUser.getId());
    for (Picture picture : pictures) {
      if (picture.getContent() != null) {
        Path imagePath = get(IMAGEDIRECTORY).normalize().resolve(picture.getContent());
        if (Files.exists(imagePath)) {
          Files.delete(imagePath);
        } else {
          throw new PictureNotFoundException(picture.getContent() + " was not found on the server");
        }
      }
      adminRepository.deletePictureById(picture.getId());
    }
    List<Like> likes = adminRepository.findAllLikesByFk(selectedUser.getId());
    for (Like like : likes) {
      adminRepository.deleteLikeById(like.getId());
    }
    List<Match> matches = adminRepository.findAllMatchesByFk(selectedUser.getId());
    for (Match match : matches) {
      adminRepository.deleteMatchById(match.getId());
    }
    List<Message> messages = adminRepository.findAllMessagesByFk(selectedUser.getId());
    for (Message message : messages) {
      adminRepository.deleteMessageById(message.getId());
    }
    adminRepository.deleteTokenByFk(selectedUser.getId());
    adminRepository.deleteUserByEmail(email);
  }
}
