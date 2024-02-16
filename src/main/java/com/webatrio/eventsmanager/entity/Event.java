package com.webatrio.eventsmanager.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
@Entity
@Table(name = "Event")
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "start_date", nullable = false)
    private Date startDateTIme;
    @Column(name = "end_time", nullable = false)
    private Date endDateTime;
    @Column(name = "location", nullable = false)
    private String location;
    @Column(name = "capacity", nullable = false)
    private int capacity;

    @ManyToMany
    @JoinTable(
            name = "event_participant",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private Set<User> participants = new HashSet<>();

}
