package com.packages.backend.pictures;

public class PictureNotFoundException extends RuntimeException{
  public PictureNotFoundException(String message) {
    super(message);
  }
}
