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

    public List<Event> findFutureEventsByLocation(Optional<String> location, Pageable pageable) {
        return eventRepository.findAll(pageable).stream().filter(event -> event.getLocation().equals(location.get())).filter(event -> event.getStartDateTIme().toInstant().isAfter(Instant.now())).collect(Collectors.toList());
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
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
