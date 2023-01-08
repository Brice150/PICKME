package com.packages.backend.pictures;

import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.nio.file.Files.copy;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("/picture")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class PictureController {
  private final PictureService pictureService;
  private final UserService userService;
  public static final String IMAGEDIRECTORY = "src/main/resources/pictures";

  public PictureController(PictureService pictureService, UserService userService) {
    this.pictureService = pictureService;
    this.userService = userService;
  }

  @GetMapping("/find/all/{id}")
  public ResponseEntity<List<Picture>> getAllPictures(@PathVariable("id") Long id) {
    List<Picture> pictures = pictureService.findAllPictures();
    pictures.removeIf(picture -> !id.equals(picture.getFkUser().getId()));
    return new ResponseEntity<>(pictures, HttpStatus.OK);
  }

  @GetMapping("/get/{content}")
  public ResponseEntity<Resource> getPicture(@PathVariable("content") String content) throws IOException {
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

  @PostMapping("/add")
  public ResponseEntity<Picture> addPicture(@RequestParam("content")List<MultipartFile> multipartImages) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser != null) {
      List<String> contents = new ArrayList<>();
      String content = null;
      for (MultipartFile image : multipartImages) {
        content = StringUtils.cleanPath(image.getOriginalFilename());
        Path fileStorage = get(IMAGEDIRECTORY, content).normalize();
        copy(image.getInputStream(), fileStorage, REPLACE_EXISTING);
        contents.add(content);
      }
      if (content == null) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }
      Picture newPicture = new Picture(content, connectedUser);
      pictureService.addPicture(newPicture);
      return new ResponseEntity<>(newPicture, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @PutMapping("/update/{content}")
  public ResponseEntity<Picture> updatePicture(@PathVariable("content") String content) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Picture picture = pictureService.findPictureByContent(content);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      connectedUser.setMainPicture(picture.getContent());
      userService.updateUser(connectedUser);
      return new ResponseEntity<>(picture, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping("/delete/{content}")
  public ResponseEntity<?> deletePicture(@PathVariable("content") String content) throws IOException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Picture picture = pictureService.findPictureByContent(content);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      if (content != null) {
        if (content.equals(connectedUser.getMainPicture())) {
          connectedUser.setMainPicture(null);
          userService.updateUser(connectedUser);
        }
        Path imagePath = get(IMAGEDIRECTORY).normalize().resolve(content);
        if (Files.exists(imagePath)) {
          Files.delete(imagePath);
        }
        else {
          throw new PictureNotFoundException(content + " was not found on the server");
        }
      }
      pictureService.deletePictureByContent(content);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}

