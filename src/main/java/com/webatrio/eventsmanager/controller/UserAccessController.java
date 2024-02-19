package com.webatrio.eventsmanager.controller;

import com.webatrio.eventsmanager.entity.Event;
import com.webatrio.eventsmanager.entity.dto.EntityResponse;
import com.webatrio.eventsmanager.entity.dto.UserRegisterRequestDTO;
import com.webatrio.eventsmanager.security.JWTTokenUtil;
import com.webatrio.eventsmanager.security.dto.AuthenticationRequest;
import com.webatrio.eventsmanager.security.dto.AuthenticationResponse;
import com.webatrio.eventsmanager.service.EventService;
import com.webatrio.eventsmanager.service.RoleService;
import com.webatrio.eventsmanager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserAccessController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    @Autowired
    private JWTTokenUtil jwtTokenUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    EventService eventService;

    @Autowired
    RoleService roleService;

    @Tag(name = "Participant")
    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<Object> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
            throws Exception {

        try {
            authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        } catch (Exception e) {
            return EntityResponse.generateResponse("Authentication", HttpStatus.UNAUTHORIZED,
                    "Invalid credentials, please check details and try again.");
        }
        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtTokenUtil.generateToken(userDetails);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

        return EntityResponse.generateResponse("Authentication", HttpStatus.OK,
                new AuthenticationResponse(token, refreshToken));

    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e) {
            throw new Exception("INVALID_CREDENTIALS", e.getCause());

        }
    }

    @Tag(name = "Participant")
    @PostMapping("register")
    public ResponseEntity<Object> register(@RequestBody UserRegisterRequestDTO request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        return EntityResponse.generateResponse("Register User", HttpStatus.OK, userService.createUser(request));
    }

    @Tag(name = "Participant")
    @GetMapping("profile")
    public ResponseEntity<Object> retrieveUserProfile() {
        return EntityResponse.generateResponse("User Profile", HttpStatus.OK, userService.findCurrentUser());
    }

    @Tag(name = "Participant")
    @GetMapping("user-events")
    public ResponseEntity<Object> getEventsForParticipant(@SortDefault(sort = "capacity") @PageableDefault(size = 20) final Pageable pageable) {
        Long participantId = userService.findCurrentUser().getId();
        return EntityResponse.generateResponse("User events", HttpStatus.OK, userService.getEventsForParticipant(participantId, pageable));
    }

    @Tag(name = "Inscription")
    @PostMapping("/participate/{eventId}")
    @Operation(summary = "Add user to an event")
    public ResponseEntity<?> participateInEvent(@PathVariable Long eventId) {
        userService.participateInEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @Tag(name = "Inscription")
    @PostMapping("cancel-inscription/{eventId}")
    public ResponseEntity<Object> cancelInscription(@PathVariable Long eventId) {
        Event event = eventService.findEventById(eventId).get();
        return EntityResponse.generateResponse("Participation removed", HttpStatus.OK, userService.deleteParticipation(event));
    }

    @GetMapping("role-list")
    public ResponseEntity<Object> getAllRoleList() {
        return EntityResponse.generateResponse("Admin Fetch Role List", HttpStatus.OK,
                roleService.findAllRole());
    }

//    @GetMapping("user-list")
//    public ResponseEntity<Object> getAllUserList(){
//        return EntityResponse.generateResponse("Admin Fetch User List", HttpStatus.OK,
//                userService.retrieveAllUserList());
//    }

}
