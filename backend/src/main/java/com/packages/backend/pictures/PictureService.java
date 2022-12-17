package com.packages.backend.pictures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

  public Picture findPictureById(Long id) {
    return pictureRepository.findPictureById(id)
      .orElseThrow(() -> new PictureNotFoundException("Picture by id " + id + " was not found"));
  }

  @Transactional
  public void deletePictureById(Long id) {
    pictureRepository.deletePictureById(id);
  }
}

