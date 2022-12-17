package com.packages.backend.likes;

public class LikeNotFoundException extends RuntimeException{
  public LikeNotFoundException(String message) {
    super(message);
  }
}
