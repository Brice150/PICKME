package com.packages.backend.service;

import com.packages.backend.exception.PictureNotFoundException;
import com.packages.backend.model.Picture;
import com.packages.backend.model.user.User;
import com.packages.backend.repository.PictureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class PictureService {
  private final PictureRepository pictureRepository;
  private final UserService userService;
  public static final String IMAGEDIRECTORY = "src/main/resources/pictures";

  @Autowired
  public PictureService(PictureRepository pictureRepository, UserService userService) {
    this.pictureRepository = pictureRepository;
    this.userService = userService;
  }

  public Optional<Picture> addPicture(List<MultipartFile> multipartImages) throws IOException {
    User connectedUser = userService.getConnectedUser();
    List<Picture> pictures = connectedUser.getPictures();
    List<String> contents = new ArrayList<>();
    String content = null;
    for (MultipartFile image : multipartImages) {
      content = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));
      Path fileStorage = get(IMAGEDIRECTORY, content).normalize();
      for (Picture picture : pictures) {
        if (picture.getContent().equals(content)) {
          return Optional.empty();
        }
      }
      copy(image.getInputStream(), fileStorage, REPLACE_EXISTING);
      contents.add(content);
    }
    if (content == null) {
      return Optional.empty();
    }
    Picture newPicture = new Picture(content, false, connectedUser);
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

  public List<Picture> getUserPicturesByFk(Long fkUser) {
    return pictureRepository.getUserPicturesByFk(fkUser)
      .orElseThrow(() -> new PictureNotFoundException("User pictures were not found"));
  }

  public String selectMainPictureById(Long pictureId) {
    User connectedUser = userService.getConnectedUser();
    Picture picture = getPictureById(pictureId);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      List<Picture> userPictures = getUserPicturesByFk(connectedUser.getId());
      userPictures.forEach(userPicture -> userPicture.setMainPicture(Objects.equals(userPicture.getId(), picture.getId())));
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

