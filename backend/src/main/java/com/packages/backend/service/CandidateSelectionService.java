package com.packages.backend.service;

import com.packages.backend.model.dto.UserDTO;

import java.util.List;

public interface CandidateSelectionService {

  /**
   * Returns one page of candidates for the connected user, sorted by distance then by affinity.
   * <p>
   * Only the main picture of each candidate is exposed: the whole album is loaded on demand by
   * {@link PictureService#getUserPictures(Long)}.
   *
   * @param page zero based page number, {@code null} being handled as the first page
   * @return at most one page of candidates
   */
  List<UserDTO> getAllSelectedUsers(Integer page);
}
