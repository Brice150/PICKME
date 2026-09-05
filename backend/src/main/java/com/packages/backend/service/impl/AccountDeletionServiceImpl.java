package com.packages.backend.service.impl;

import com.packages.backend.model.entity.Notification;
import com.packages.backend.model.entity.User;
import com.packages.backend.model.enums.UserRole;
import com.packages.backend.repository.NotificationRepository;
import com.packages.backend.repository.UserRepository;
import com.packages.backend.service.AccountDeletionService;
import com.packages.backend.service.DeletedAccountService;
import com.packages.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AccountDeletionServiceImpl implements AccountDeletionService {

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final UserService userService;
  private final DeletedAccountService deletedAccountService;

  public AccountDeletionServiceImpl(UserRepository userRepository, NotificationRepository notificationRepository, UserService userService, DeletedAccountService deletedAccountService) {
    this.userRepository = userRepository;
    this.notificationRepository = notificationRepository;
    this.userService = userService;
    this.deletedAccountService = deletedAccountService;
  }

  @Override
  @Transactional
  public void deleteConnectedUser() {
    User connectedUser = userService.getConnectedUser();
    deleteUser(connectedUser, connectedUser);
  }

  @Override
  @Transactional
  public void deleteUserById(Long userId) {
    User connectedUser = userService.getConnectedUser();
    User selectedUser = userService.getUserById(userId);
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
    String content = connectedUser.getUserRole() == UserRole.ROLE_USER
      ? user.getNickname() + " has deleted his account"
      : "Admin has deleted " + user.getNickname() + "'s account";
    List<Notification> notifications = matchedUsers.stream()
      .map(match -> new Notification(content, "delete", new Date(), false, match))
      .toList();
    notificationRepository.saveAll(notifications);
  }
}
