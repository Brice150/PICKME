package com.packages.backend.pictures;

import com.packages.backend.user.User;
import com.packages.backend.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/picture")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class PictureController {
  private final PictureService pictureService;
  private final UserService userService;

  public PictureController(PictureService pictureService, UserService userService) {
    this.pictureService = pictureService;
    this.userService = userService;
  }

  @GetMapping("/find/{id}")
  public ResponseEntity<Picture> getPictureById(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Picture picture = pictureService.findPictureById(id);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      return new ResponseEntity<>(picture, HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @PostMapping("/add")
  public ResponseEntity<Picture> addPicture(@RequestBody Picture picture) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      Picture newPicture = pictureService.addPicture(picture);
      return new ResponseEntity<>(newPicture, HttpStatus.CREATED);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<?> deletePicture(@PathVariable("id") Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentUserEmail = authentication.getName();
    User connectedUser = userService.findUserByEmail(currentUserEmail);
    Picture picture = pictureService.findPictureById(id);
    if (connectedUser.getId().equals(picture.getFkUser().getId())) {
      pictureService.deletePictureById(id);
      return new ResponseEntity<>(HttpStatus.OK);
    }
    else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }
}

