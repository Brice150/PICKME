package com.packages.backend.service.impl;

import com.packages.backend.exception.PictureNotFoundException;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.PictureRepository;
import com.packages.backend.service.PictureService;
import com.packages.backend.service.ServiceStatus;
import com.packages.backend.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PictureServiceImpl implements PictureService {

  // Maximum number of pictures an album can hold.
  private static final int MAX_PICTURES_PER_USER = 6;

  private final PictureRepository pictureRepository;
  private final UserService userService;

  public PictureServiceImpl(PictureRepository pictureRepository, UserService userService) {
    this.pictureRepository = pictureRepository;
    this.userService = userService;
  }

  @Override
  @Transactional
  public Optional<Picture> addPicture(String pictureContent) {
    User connectedUser = userService.getConnectedUser();
    List<Picture> pictures = connectedUser.getPictures();
    if (pictures.size() >= MAX_PICTURES_PER_USER || pictures.stream().anyMatch(previousPicture -> previousPicture.getContent().equals(pictureContent))) {
      return Optional.empty();
    }
    Picture newPicture = new Picture(pictureContent, pictures.isEmpty(), connectedUser);
    pictureRepository.save(newPicture);
    return Optional.of(newPicture);
  }

  @Override
  public Picture getPictureById(Long pictureId) {
    return pictureRepository.findPictureById(pictureId)
      .orElseThrow(() -> new PictureNotFoundException("Picture by id " + pictureId + " was not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Picture> getUserPictures(Long userId) {
    return pictureRepository.findAllByUserId(userId);
  }

  @Override
  @Transactional
  public ServiceStatus selectMainPictureById(Long pictureId) {
    User connectedUser = userService.getConnectedUser();
    Picture picture = getPictureById(pictureId);
    if (connectedUser.getId().equals(picture.getFkUser().getId()) && Boolean.FALSE.equals(picture.getIsMainPicture())) {
      List<Picture> userPictures = connectedUser.getPictures();
      userPictures.forEach(userPicture -> userPicture.setIsMainPicture(Objects.equals(userPicture.getId(), picture.getId())));
      pictureRepository.saveAll(userPictures);
      return ServiceStatus.OK;
    }
    return ServiceStatus.FORBIDDEN;
  }

  @Override
  @Transactional
  public ServiceStatus deletePictureById(Long pictureId) {
    User connectedUser = userService.getConnectedUser();
    Picture picture = getPictureById(pictureId);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      pictureRepository.deletePictureById(pictureId);
      return ServiceStatus.OK;
    }
    return ServiceStatus.FORBIDDEN;
  }
}
