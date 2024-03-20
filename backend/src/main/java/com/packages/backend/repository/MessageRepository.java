package com.packages.backend.repository;

import com.packages.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

  void deleteMessageById(Long id);

  Optional<Message> findMessageById(Long id);

  @Query(
    "SELECT DISTINCT m FROM Message m" +
      " WHERE (m.fkSender = :connectedId AND m.fkReceiver = :userId)" +
      " OR (m.fkSender = :userId AND m.fkReceiver = :connectedId)" +
      " ORDER BY m.date ASC"
  )
  List<Message> getUserMessagesByFk(@Param("connectedId") Long connectedId, @Param("userId") Long userId);
}
