package com.packages.backend.repository;

import com.packages.backend.model.entity.DeletedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedAccountRepository extends JpaRepository<DeletedAccount, Long> {
}
