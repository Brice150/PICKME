package com.packages.backend.repository;

import com.packages.backend.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

  void deleteMessageById(Long id);

  Optional<Message> findMessageById(Long id);
}
