package com.packages.backend.service;

import com.packages.backend.model.Match;
import com.packages.backend.model.dto.UserDTO;
import com.packages.backend.model.dto.UserUpdateRequest;
import com.packages.backend.model.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

  /**
   * Persists a user whose fields have already been validated by the registration form, the
   * uniqueness of the email being the only rule left because it needs the database.
   *
   * @param user user to register
   * @return {@link ServiceStatus#OK} when the account has been created, otherwise the reason of
   * the rejection
   */
  RegistrationResult signUpUser(User user);


  /**
   * Returns every match of the connected user with the conversation attached to it.
   *
   * @return the matches, most recently active first
   */
  List<Match> getAllUserMatches();

  /**
   * Applies the submitted fields to the connected account, leaving the others untouched.
   *
   * @param request fields the owner of the account is allowed to change
   * @return the updated account
   */
  UserDTO updateUser(UserUpdateRequest request);

  /**
   * Finds a user from its identifier.
   *
   * @param userId identifier of the user
   * @return the matching user
   * @throws com.packages.backend.exception.UserNotFoundException when no user matches
   */
  User getUserById(Long userId);

  /**
   * Finds a user from its email.
   *
   * @param email email of the user
   * @return the matching user
   * @throws com.packages.backend.exception.UserNotFoundException when no user matches
   */
  User getUserByEmail(String email);

  /**
   * Returns the user owning the current security context.
   *
   * @return the connected user
   */
  User getConnectedUser();

  /**
   * Returns the complete view of the connected user.
   *
   * @return the connected user
   */
  UserDTO getConnectedUserDTO();


}
