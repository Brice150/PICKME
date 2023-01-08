package com.packages.backend.pictures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PictureService {
  private final PictureRepository pictureRepository;

  @Autowired
  public PictureService(PictureRepository pictureRepository) {
    this.pictureRepository = pictureRepository;
  }

  public Picture addPicture(Picture picture) {
    return pictureRepository.save(picture);
  }

  public List<Picture> findAllPictures() {
    return pictureRepository.findAll();
  }

  public Picture updatePicture(Picture updatePicture) {
    return pictureRepository.save(updatePicture);
  }

  public Picture findPictureByContent(String content) {
    return pictureRepository.findPictureByContent(content)
      .orElseThrow(() -> new PictureNotFoundException("Picture by content " + content + " was not found"));
  }

  @Transactional
  public void deletePictureByContent(String content) {
    pictureRepository.deletePictureByContent(content);
  }
}

