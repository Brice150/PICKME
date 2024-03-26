package com.packages.backend.model;

import com.packages.backend.model.entity.Message;
import com.packages.backend.model.dto.UserDTO;

import java.util.List;

public class Match {
  private UserDTO user;
  private List<Message> messages;

  public Match() {
  }

  public Match(UserDTO user, List<Message> messages) {
    this.user = user;
    this.messages = messages;
  }

  public UserDTO getUser() {
    return user;
  }

  public void setUser(UserDTO user) {
    this.user = user;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
