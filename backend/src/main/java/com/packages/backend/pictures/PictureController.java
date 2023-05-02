package com.packages.backend.pictures;

import org.springframework.core.io.Resource;
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

  @GetMapping("/all/{fkUser}")
  public ResponseEntity<List<Picture>> getAllUserPictures(@PathVariable("fkUser") Long fkUser) {
    List<Picture> pictures = pictureService.findAllPicturesByFk(fkUser);
    return new ResponseEntity<>(pictures, HttpStatus.OK);
  }

  @GetMapping("/{content}")
  public ResponseEntity<Resource> getPicture(@PathVariable("content") String content) throws IOException {
    return pictureService.getResource(content);
  }

  @PostMapping()
  public ResponseEntity<Picture> addPicture(@RequestParam("content") List<MultipartFile> multipartImages) throws IOException {
    Optional<Picture> addedPicture = pictureService.addPicture(multipartImages);
    return addedPicture.map(picture -> new ResponseEntity<>(picture, HttpStatus.CREATED)).orElseGet(() -> new ResponseEntity<>(HttpStatus.FORBIDDEN));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> deletePicture(@PathVariable("id") Long id) throws IOException {
    return "OK".equals(pictureService.deletePictureById(id)) ?
      new ResponseEntity<>(HttpStatus.OK) :
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
  }
}

