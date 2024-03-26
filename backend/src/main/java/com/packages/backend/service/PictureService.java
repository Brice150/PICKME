package com.packages.backend.service;

import com.packages.backend.exception.PictureNotFoundException;
import com.packages.backend.model.entity.Picture;
import com.packages.backend.model.entity.User;
import com.packages.backend.repository.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PictureService {
  private final PictureRepository pictureRepository;
  private final UserService userService;

  @Autowired
  public PictureService(PictureRepository pictureRepository, UserService userService) {
    this.pictureRepository = pictureRepository;
    this.userService = userService;
  }

  public Optional<Picture> addPicture(String pictureContent) {
    User connectedUser = userService.getConnectedUser();
    List<Picture> pictures = connectedUser.getPictures();
    if (pictures.size() > 5 || pictures.stream().anyMatch(previousPicture -> previousPicture.getContent().equals(pictureContent))) {
      return Optional.empty();
    }
    Picture newPicture = new Picture(pictureContent, pictures.isEmpty(), connectedUser);
    pictureRepository.save(newPicture);
    return Optional.of(newPicture);
  }

  public Picture getPictureById(Long pictureId) {
    return pictureRepository.findPictureById(pictureId)
      .orElseThrow(() -> new PictureNotFoundException("Picture by id " + pictureId + " was not found"));
  }

  public String selectMainPictureById(Long pictureId) {
    User connectedUser = userService.getConnectedUser();
    Picture picture = getPictureById(pictureId);
    if (connectedUser.getId().equals(picture.getFkUser().getId()) && Boolean.FALSE.equals(picture.getIsMainPicture())) {
      List<Picture> userPictures = connectedUser.getPictures();
      userPictures.forEach(userPicture -> userPicture.setIsMainPicture(Objects.equals(userPicture.getId(), picture.getId())));
      pictureRepository.saveAll(userPictures);
      return "OK";
    } else {
      return "FORBIDDEN";
    }
  }

  @Transactional
  public String deletePictureById(Long pictureId) {
    User connectedUser = userService.getConnectedUser();
    Picture picture = getPictureById(pictureId);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      pictureRepository.deletePictureById(pictureId);
      return "OK";
    } else {
      return "FORBIDDEN";
    }
  }
}

