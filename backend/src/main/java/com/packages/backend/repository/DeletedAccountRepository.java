package com.packages.backend.repository;

import com.packages.backend.model.entity.DeletedAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeletedAccountRepository extends JpaRepository<DeletedAccount, Long> {

  /**
   * Reads one page of archived accounts, ordered by the {@link Pageable}.
   *
   * @param email    fragment of email to search on, empty to match every account
   * @param pageable page and sort order to apply
   * @return at most one page of archived accounts
   */
  @Query(
    "SELECT d FROM DeletedAccount d " +
      " WHERE LOWER(d.email) LIKE CONCAT('%', LOWER(:email), '%')"
  )
  List<DeletedAccount> getAllDeletedAccounts(@Param("email") String email, Pageable pageable);
}
