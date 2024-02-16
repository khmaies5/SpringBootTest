package com.webatrio.eventsmanager;

import com.webatrio.eventsmanager.controller.UserAccessController;
import com.webatrio.eventsmanager.entity.Event;
import com.webatrio.eventsmanager.repository.IEventRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class ParticipantControllerTest {
    @Mock
    private IEventRepository eventRepository;

    @InjectMocks
    private UserAccessController participantController;

    @Test
    public void testGetEventsForParticipant() {
        // Mock data
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Event 1");

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Event 2");

        List<Event> events = Arrays.asList(event1, event2);
        Page<Event> page = new PageImpl<>(events);

        // Mock the repository method
        when(eventRepository.findByParticipantId(anyLong(), any(Pageable.class))).thenReturn(page);

        // Call the controller method
        ResponseEntity<Page<Event>> response = participantController.getEventsForParticipant(Pageable.unpaged());

        // Check the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(events.size(), response.getBody().size());
        assertEquals(events, response.getBody());
    }
}
