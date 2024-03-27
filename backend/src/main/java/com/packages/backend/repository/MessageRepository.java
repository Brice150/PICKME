package com.packages.backend.repository;

import com.packages.backend.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
  Optional<Message> findMessageById(Long id);

  @Query(
    "SELECT DISTINCT m FROM Message m" +
      " WHERE (m.fkSender = :connectedId AND m.fkReceiver = :userId)" +
      " OR (m.fkSender = :userId AND m.fkReceiver = :connectedId)" +
      " ORDER BY m.date ASC"
  )
  List<Message> getUserMessagesByFk(@Param("connectedId") Long connectedId, @Param("userId") Long userId);

  @Transactional
  @Modifying
  @Query(
    "DELETE FROM Message m" +
      " WHERE (m.fkSender = :connectedId AND m.fkReceiver = :userId)" +
      " OR (m.fkSender = :userId AND m.fkReceiver = :connectedId)"
  )
  void deleteMessagesByFk(@Param("connectedId") Long connectedId, @Param("userId") Long userId);
}
