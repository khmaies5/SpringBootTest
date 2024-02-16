package com.webatrio.eventsmanager.repository;

import com.webatrio.eventsmanager.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e JOIN e.participants p WHERE p.id = :participantId")
    Page<Event> findByParticipantId(Long participantId, Pageable pageable);
}
