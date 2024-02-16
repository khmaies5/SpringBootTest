package com.webatrio.eventsmanager.repository;

import com.webatrio.eventsmanager.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    @Query("SELECT p FROM User p JOIN p.events e WHERE e.id = :eventId")
    Page<User> findByEventId(Long eventId, Pageable pageable);
    User findByUsername(String username);
}
