package com.webatrio.eventsmanager.service;

import com.webatrio.eventsmanager.entity.Event;
import com.webatrio.eventsmanager.entity.User;
import com.webatrio.eventsmanager.repository.IEventRepository;
import com.webatrio.eventsmanager.repository.IUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventService {


    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private IUserRepository userRepository;

    public List<Event> findFutureEventsByLocation(String location, Pageable pageable) {

        System.out.println("location : " + location);
        return eventRepository.findAll(pageable).stream()
                .filter(event -> {
                    if (location.isEmpty()) {
                        return true;
                    } else {
                        return event.getLocation().equalsIgnoreCase(location);
                    }
                }) // Filter by location
                .filter(event -> event.getStartDateTime().toInstant().isAfter(Instant.now())) // Filter future events
                .collect(Collectors.toList());

    }

    public Event updateEvent(Event event) {
        if (eventRepository.existsById(event.getId())) {
            return eventRepository.save(event);
        } else {
            return null;
        }
    }

    public Event createNewEvent(Event event) {
        return eventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }

    public Page<User> findEventParticipants(Long id, Pageable pageable) {

        return userRepository.findByEventId(id, pageable);
    }

}
