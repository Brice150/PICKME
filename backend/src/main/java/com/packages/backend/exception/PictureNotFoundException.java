package com.packages.backend.exception;

public class PictureNotFoundException extends RuntimeException {
  public PictureNotFoundException(String message) {
    super(message);
  }
}
