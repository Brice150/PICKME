package com.packages.backend.repository;

import com.packages.backend.model.entity.Picture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PictureRepository extends JpaRepository<Picture, Long> {

  /**
   * Deletes a picture from its identifier.
   *
   * @param id identifier of the picture
   */
  void deletePictureById(Long id);

  /**
   * Finds a picture from its identifier.
   *
   * @param id identifier of the picture
   * @return the matching picture, or an empty optional when none exists
   */
  Optional<Picture> findPictureById(Long id);

  // The selection screen only displays one picture per candidate: loading the whole album of every
  // candidate would multiply the size of the response by the number of pictures per profile.
  // The sub query keeps the main picture, or the most recent one when the album has lost its main
  // picture, which mirrors the order declared on the album of a user.
  @Query(
    "SELECT p FROM Picture p WHERE p.id IN (" +
      " SELECT MAX(main.id) FROM Picture main" +
      " WHERE main.fkUser.id IN :userIds" +
      " AND (main.isMainPicture = TRUE OR NOT EXISTS (" +
      "   SELECT other.id FROM Picture other" +
      "   WHERE other.fkUser.id = main.fkUser.id AND other.isMainPicture = TRUE))" +
      " GROUP BY main.fkUser.id)"
  )
  List<Picture> findDisplayedPicturesByUserIds(@Param("userIds") Collection<Long> userIds);

  @Query("SELECT p FROM Picture p WHERE p.fkUser.id = :userId ORDER BY p.isMainPicture DESC, p.id DESC")
  List<Picture> findAllByUserId(@Param("userId") Long userId);
}
