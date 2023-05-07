package com.packages.backend.pictures;

import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

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
    User connectedUser = userService.findConnectedUser();
    List<Picture> pictures = findAllPictures();
    if (connectedUser != null) {
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
      Picture newPicture = new Picture(content, connectedUser);
      if (connectedUser.getMainPicture() == null) {
        connectedUser.setMainPicture(newPicture.getContent());
        userService.updateUser(connectedUser);
      }
      pictureRepository.save(newPicture);
      return Optional.of(newPicture);
    } else {
      return Optional.empty();
    }
  }

  public List<Picture> findAllPicturesByFk(Long fkUser) {
    return pictureRepository.findAllPicturesByFk(fkUser);
  }

  public List<Picture> findAllPictures() {
    return pictureRepository.findAll();
  }

  public Picture findPictureById(Long id) {
    return pictureRepository.findPictureById(id)
      .orElseThrow(() -> new PictureNotFoundException("Picture by id " + id + " was not found"));
  }

  public ResponseEntity<Resource> getResource(String content) throws IOException {
    Path imagePath = get(IMAGEDIRECTORY).normalize().resolve(content);
    if (!Files.exists(imagePath)) {
      throw new FileNotFoundException(content + " was not found on the server");
    }
    Resource resource = new UrlResource(imagePath.toUri());
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("File-Name", content);
    httpHeaders.add(CONTENT_DISPOSITION, "attachment;File-Name=" + resource.getFilename());
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(imagePath)))
      .headers(httpHeaders).body(resource);
  }

  @Transactional
  public String deletePictureById(Long id) throws IOException {
    User connectedUser = userService.findConnectedUser();
    Picture picture = findPictureById(id);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      if (picture.getContent() != null) {
        if (picture.getContent().equals(connectedUser.getMainPicture())) {
          connectedUser.setMainPicture(null);
          userService.updateUser(connectedUser);
        }
        Path imagePath = get(IMAGEDIRECTORY).normalize().resolve(picture.getContent());
        Files.deleteIfExists(imagePath);
      }
      pictureRepository.deletePictureById(id);
      return "OK";
    } else {
      return "FORBIDDEN";
    }
  }
}

