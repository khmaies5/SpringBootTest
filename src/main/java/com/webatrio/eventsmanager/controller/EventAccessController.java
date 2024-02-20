package com.webatrio.eventsmanager.controller;

import com.webatrio.eventsmanager.entity.Event;
import com.webatrio.eventsmanager.entity.dto.EntityResponse;
import com.webatrio.eventsmanager.service.EventService;
import com.webatrio.eventsmanager.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("event")
public class EventAccessController {
    @Autowired
    EventService eventService;

    @Autowired
    UserService userService;

    @Tag(name = "Events")
    @RequestMapping(value = "/add-event", method = RequestMethod.POST)
    public ResponseEntity<Object> addEvent(@RequestBody Event event) {
        return EntityResponse.generateResponse("Add Event", HttpStatus.OK, eventService.createNewEvent(event));
    }

    @Tag(name = "Events")
    @RequestMapping(value = "/list-incoming-events", method = RequestMethod.GET)
    public ResponseEntity<Object> getEventsByLocation(@SortDefault(sort = "location") @PageableDefault(size = 20) final Pageable pageable, @RequestParam(defaultValue = "") String location) {
        System.out.println("location : " + location);

        return EntityResponse.generateResponse("Incoming Events", HttpStatus.OK, eventService.findFutureEventsByLocation(location, pageable));
    }

    @Tag(name = "Events")
    @RequestMapping(value = "/delete-event", method = RequestMethod.POST)
    public ResponseEntity<Object> deleteEvent(@RequestBody Long id) {
        if (eventService.findEventById(id).isPresent()) {
            eventService.deleteEvent(id);
            return ResponseEntity.ok("DELETED");
        } else {
            return ResponseEntity.ok("item not found");
        }
    }

    @Tag(name = "Events")
    @RequestMapping(value = "/edit-event", method = RequestMethod.POST)
    public ResponseEntity<Object> updateEvent(@RequestBody Event event) {
        Event response = eventService.updateEvent(event);
        if (response == null) {
            return EntityResponse.generateResponse("Event not found", HttpStatus.OK, null);
        } else {
            return EntityResponse.generateResponse("Event", HttpStatus.OK, response);

        }
    }

    @Tag(name = "Inscription")
    @PostMapping(value = "/list-event-users/{eventId}")
    public ResponseEntity<Object> listEventParticipant(@SortDefault(sort = "username") @PageableDefault(size = 20) final Pageable pageable, @PathVariable Long eventId) {
        return EntityResponse.generateResponse("Event participants", HttpStatus.OK, eventService.findEventParticipants(eventId, pageable));
    }


}
