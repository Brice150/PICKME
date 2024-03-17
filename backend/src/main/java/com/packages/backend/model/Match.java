package com.packages.backend.model;

import com.packages.backend.model.user.User;

import java.util.List;

public class Match {
  private User user;
  private List<Message> messages;

  public Match() {
  }

  public Match(User user, List<Message> messages) {
    this.user = user;
    this.messages = messages;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }
}
