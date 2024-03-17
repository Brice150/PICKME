package com.packages.backend.controller;

import com.packages.backend.model.Picture;
import com.packages.backend.service.PictureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
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
  public ResponseEntity<Picture> addPicture(@RequestParam("content") List<MultipartFile> multipartImages) throws IOException {
    Optional<Picture> addedPicture = pictureService.addPicture(multipartImages);
    return addedPicture.map(picture -> new ResponseEntity<>(picture, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
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

