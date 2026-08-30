package com.packages.backend.service;

import com.packages.backend.model.entity.Picture;

import java.util.List;
import java.util.Optional;

public interface PictureService {

  /**
   * Adds a picture to the album of the connected user, the first one becoming the main picture.
   *
   * @param pictureContent base64 content of the picture
   * @return the persisted picture, or an empty optional when the album is full or already holds
   * that picture
   */
  Optional<Picture> addPicture(String pictureContent);

  /**
   * Finds a picture from its identifier.
   *
   * @param pictureId identifier of the picture
   * @return the matching picture
   * @throws com.packages.backend.exception.PictureNotFoundException when no picture matches
   */
  Picture getPictureById(Long pictureId);

  /**
   * Returns the whole album of a user, main picture first. The selection screen only receives the
   * main picture of each candidate and calls this method when a profile is opened.
   *
   * @param userId identifier of the user
   * @return the pictures of the user
   */
  List<Picture> getUserPictures(Long userId);

  /**
   * Promotes a picture of the connected user as its main picture.
   *
   * @param pictureId identifier of the picture
   * @return {@link ServiceStatus#OK} or {@link ServiceStatus#FORBIDDEN} when the picture does not
   * belong to the connected user
   */
  String selectMainPictureById(Long pictureId);

  /**
   * Removes a picture from the album of the connected user.
   *
   * @param pictureId identifier of the picture
   * @return {@link ServiceStatus#OK} or {@link ServiceStatus#FORBIDDEN} when the picture does not
   * belong to the connected user
   */
  String deletePictureById(Long pictureId);
}
