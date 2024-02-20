package com.webatrio.eventsmanager.service;

import com.webatrio.eventsmanager.entity.Event;
import com.webatrio.eventsmanager.entity.Role;
import com.webatrio.eventsmanager.entity.User;
import com.webatrio.eventsmanager.entity.UserRole;
import com.webatrio.eventsmanager.entity.dto.UserRegisterRequestDTO;
import com.webatrio.eventsmanager.repository.IEventRepository;
import com.webatrio.eventsmanager.repository.IUserRepository;
import com.webatrio.eventsmanager.repository.IUserRoleRepository;
import com.webatrio.eventsmanager.security.SecurityPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Primary
@Transactional
public class UserService implements UserDetailsService {
    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IEventRepository eventRepository;

    @Autowired
    private IUserRoleRepository userRoleRepository;

    @Autowired
    RoleService roleService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            List<UserRole> userRoles = userRoleRepository.findAllByUserId(user.getId());

            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

            userRoles.forEach(userRole -> {
                authorities.add(new SimpleGrantedAuthority(userRole.getRole().getName()));
            });

            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
        }
        return null;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public String createUser(UserRegisterRequestDTO request) {
        try {
            User user = (User) dtoMapperRequestDtoToUser(request);

            user = userRepository.save(user);
            if (!request.getRoleList().isEmpty()) {
                for (String role : request.getRoleList()) {
                    Role existingRole = roleService.findRoleByName("ROLE_" + role.toUpperCase());
                    if (existingRole != null) {
                        addUserRole(user, existingRole);
                    }
                }
            } else {
                addUserRole(user, null);
            }

            return "User successfully created.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getCause().getMessage();
        }
    }

    public List<User> retrieveAllUserList() {
        return userRepository.findAll();
    }

    public User updateUser(UserRegisterRequestDTO userRequestDTO) {
        User user = (User) dtoMapperRequestDtoToUser(userRequestDTO);

        user = userRepository.save(user);
        addUserRole(user, null);

        return user;
    }

    public User findCurrentUser() {
        return userRepository.findById(SecurityPrincipal.getInstance().getLoggedInPrincipal().getId()).get();

    }

    public List<UserRole> findAllCurrentUserRole() {
        return userRoleRepository.findAllByUserId(SecurityPrincipal.getInstance().getLoggedInPrincipal().getId());

    }

    public Optional<User> findUserById(long id) {
        return userRepository.findById(id);
    }

    public void addUserRole(User user, Role role) {

        UserRole userRole = new UserRole();
        userRole.setUser(user);

        if (role == null) {
            role = roleService.findDefaultRole();
        }

        userRole.setRole(role);
        userRoleRepository.save(userRole);
    }

    public Page<Event> getEventsForParticipant(Long participantId, Pageable pageable) {
        return eventRepository.findByParticipantId(participantId, pageable);
    }

    @Transactional
    public Object deleteParticipation(Event event) {
        User user = findCurrentUser();

        user.getEvents().remove(event);
        event.getParticipants().remove(user);

        eventRepository.save(event);
        userRepository.save(user);

        return event;
    }

    @Transactional
    public void participateInEvent(Long eventId) {
        User participant = findCurrentUser();
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (participant != null && eventOptional.isPresent()) {

            Event event = eventOptional.get();
            if (event.getCapacity() > (long) event.getParticipants().size()) {
                event.getParticipants().add(participant);
                participant.getEvents().add(event);
                LOG.error(event.getParticipants().toString());


                userRepository.save(participant);
                eventRepository.save(event);
            } else {
                throw new IllegalArgumentException("Event at maximum capacity");

            }

        } else {
            throw new IllegalArgumentException("User or Event not found");
        }
    }

    private Object dtoMapperRequestDtoToUser(UserRegisterRequestDTO source) {
        User target = new User();
        target.setUsername(source.getUsername());
        target.setPassword(source.getPassword());
        return target;
    }
}
