package com.webatrio.eventsmanager;

import com.webatrio.eventsmanager.controller.UserAccessController;
import com.webatrio.eventsmanager.entity.Event;
import com.webatrio.eventsmanager.entity.User;
import com.webatrio.eventsmanager.security.JWTTokenUtil;
import com.webatrio.eventsmanager.service.EventService;
import com.webatrio.eventsmanager.service.RoleService;
import com.webatrio.eventsmanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = UserAccessController.class)
@WithMockUser
public class ParticipantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserService userService;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    JWTTokenUtil jwtTokenUtil;

    @MockBean
    EventService eventService;

    @MockBean
    RoleService roleService;

    String exampleUserEvents = "{\n" +
            "    \"TimeStamp\": \"2024-02-19T16:45:01.074+00:00\",\n" +
            "    \"Message\": \"User events\",\n" +
            "    \"Status\": 200,\n" +
            "    \"Data\": {\n" +
            "        \"content\": [\n" +
            "            {\n" +
            "                \"id\": 1,\n" +
            "                \"title\": \"test 1\",\n" +
            "                \"description\": \"this is for testing\",\n" +
            "                \"startDateTime\": \"2021-12-17T15:59:19.516+00:00\",\n" +
            "                \"endDateTime\": \"2023-12-17T15:59:19.516+00:00\",\n" +
            "                \"location\": \"web atrio\",\n" +
            "                \"capacity\": 20\n" +
            "            }\n" +
            "        ],\n" +
            "        \"pageable\": {\n" +
            "            \"pageNumber\": 0,\n" +
            "            \"pageSize\": 20,\n" +
            "            \"sort\": {\n" +
            "                \"empty\": false,\n" +
            "                \"sorted\": true,\n" +
            "                \"unsorted\": false\n" +
            "            },\n" +
            "            \"offset\": 0,\n" +
            "            \"paged\": true,\n" +
            "            \"unpaged\": false\n" +
            "        },\n" +
            "        \"last\": true,\n" +
            "        \"totalPages\": 1,\n" +
            "        \"totalElements\": 1,\n" +
            "        \"size\": 20,\n" +
            "        \"number\": 0,\n" +
            "        \"sort\": {\n" +
            "            \"empty\": false,\n" +
            "            \"sorted\": true,\n" +
            "            \"unsorted\": false\n" +
            "        },\n" +
            "        \"numberOfElements\": 1,\n" +
            "        \"first\": true,\n" +
            "        \"empty\": false\n" +
            "    }\n" +
            "}";


    @Test
    public void getUserEvents() throws Exception {
        Event event1 = new Event();
        event1.setId(1L);
        event1.setTitle("Event 1");

        Event event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Event 2");

        User participant = new User();
        participant.setId(1L);
        participant.setUsername("test");
        participant.setPassword("1234");

        List<Event> events = Arrays.asList(event1, event2);
        Set<Event> eventsSet = new HashSet<>(events);
        participant.setEvents(eventsSet);

        Page<Event> page = new PageImpl<>(events);
        Mockito.when(userService.findCurrentUser()).thenReturn(participant);
        Mockito.when(userService.getEventsForParticipant(anyLong(), any(Pageable.class))).thenReturn(page);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/user/user-events").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        String expected = "{\"Message\":\"User events\",\"Status\":200,\"Data\":{\"content\":[{\"id\":1,\"title\":\"Event 1\",\"description\":null,\"startDateTime\":null,\"endDateTime\":null,\"location\":null,\"capacity\":0},{\"id\":2,\"title\":\"Event 2\",\"description\":null,\"startDateTime\":null,\"endDateTime\":null,\"location\":null,\"capacity\":0}],\"pageable\":\"INSTANCE\",\"last\":true,\"totalElements\":2,\"totalPages\":1,\"size\":2,\"number\":0,\"sort\":{\"empty\":true,\"sorted\":false,\"unsorted\":true},\"numberOfElements\":2,\"first\":true,\"empty\":false}}\n";

        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }
}
