package com.packages.backend.service;

import com.packages.backend.exception.PictureNotFoundException;
import com.packages.backend.model.Picture;
import com.packages.backend.model.user.User;
import com.packages.backend.repository.PictureRepository;
import com.packages.backend.repository.UserRepository;
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
  private final UserRepository userRepository;

  @Autowired
  public PictureService(PictureRepository pictureRepository, UserService userService, UserRepository userRepository) {
    this.pictureRepository = pictureRepository;
    this.userService = userService;
    this.userRepository = userRepository;
  }

  public Optional<Picture> addPicture(String pictureContent) {
    User connectedUser = userService.getConnectedUser();
    List<Picture> pictures = connectedUser.getPictures();
    if (pictures.size() > 5 || pictures.stream().anyMatch(previousPicture -> previousPicture.getContent().equals(pictureContent))) {
      return Optional.empty();
    }
    Picture newPicture = new Picture(pictureContent, pictures.isEmpty(), connectedUser);
    if (connectedUser.getMainPicture() == null) {
      connectedUser.setMainPicture(newPicture.getContent());
      userService.updateUser(connectedUser);
    }
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
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      List<Picture> userPictures = connectedUser.getPictures();
      userPictures.forEach(userPicture -> userPicture.setIsMainPicture(Objects.equals(userPicture.getId(), picture.getId())));
      pictureRepository.saveAll(userPictures);
      connectedUser.setMainPicture(picture.getContent());
      userService.updateUser(connectedUser);
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
      connectedUser.getPictures().remove(picture);
      if (Boolean.TRUE.equals(picture.getIsMainPicture())) {
        if (!connectedUser.getPictures().isEmpty()) {
          connectedUser.getPictures().get(0).setIsMainPicture(true);
          connectedUser.setMainPicture(connectedUser.getPictures().get(0).getContent());
        } else {
          connectedUser.setMainPicture(null);
        }
        userRepository.save(connectedUser);
      }
      return "OK";
    } else {
      return "FORBIDDEN";
    }
  }
}

