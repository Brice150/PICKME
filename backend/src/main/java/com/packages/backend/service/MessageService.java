package com.packages.backend.service;

import com.packages.backend.model.entity.Message;

import java.util.Optional;

public interface MessageService {

  /**
   * Sends a message to a matched user and notifies them.
   *
   * @param message message to send
   * @return the persisted message, or an empty optional when the receiver is not a match
   */
  Optional<Message> addMessage(Message message);

  /**
   * Edits the content of a message owned by the connected user.
   *
   * @param message message carrying the new content
   * @return the updated message, or an empty optional when the connected user is not its author
   */
  Optional<Message> updateMessage(Message message);

  /**
   * Finds a message from its identifier.
   *
   * @param messageId identifier of the message
   * @return the matching message
   * @throws com.packages.backend.exception.MessageNotFoundException when no message matches
   */
  Message getMessageById(Long messageId);

  /**
   * Empties the content of a message owned by the connected user, keeping it in the conversation.
   *
   * @param messageId identifier of the message
   * @return {@link ServiceStatus#OK} or {@link ServiceStatus#FORBIDDEN} when the connected user is
   * not its author
   */
  String deleteMessageById(Long messageId);
}
