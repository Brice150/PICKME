package com.packages.backend.repository;

import com.packages.backend.model.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
  @Query(
    "SELECT n FROM Notification n" +
      " WHERE n.fkUser.id = :connectedId" +
      " ORDER BY n.date DESC"
  )
  List<Notification> getAllUserNotifications(Long connectedId, Pageable pageable);
}
