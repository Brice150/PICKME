package com.packages.backend.repository;

import com.packages.backend.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AdminRepository extends JpaRepository<User, Long> {
  @Query("SELECT u FROM User u ORDER BY u.nickname")
  List<User> getAllUsers();
}
