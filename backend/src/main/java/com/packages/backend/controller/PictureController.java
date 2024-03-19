package com.packages.backend.controller;

import com.packages.backend.model.Picture;
import com.packages.backend.service.PictureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/picture")
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
public class PictureController {
  private final PictureService pictureService;


  public PictureController(PictureService pictureService) {
    this.pictureService = pictureService;
  }

  @PostMapping()
  public ResponseEntity<Picture> addPicture(@RequestBody String pictureContent) {
    Optional<Picture> addedPicture = pictureService.addPicture(pictureContent);
    return addedPicture.map(newPicture -> new ResponseEntity<>(newPicture, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  @PutMapping("/{pictureId}")
  public ResponseEntity<Void> selectMainPicture(@PathVariable("pictureId") Long pictureId) {
    return "OK".equals(pictureService.selectMainPictureById(pictureId)) ?
      new ResponseEntity<>(HttpStatus.OK) :
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }

  @DeleteMapping("/{pictureId}")
  public ResponseEntity<Void> deletePicture(@PathVariable("pictureId") Long pictureId) {
    return "OK".equals(pictureService.deletePictureById(pictureId)) ?
      new ResponseEntity<>(HttpStatus.OK) :
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }
}

