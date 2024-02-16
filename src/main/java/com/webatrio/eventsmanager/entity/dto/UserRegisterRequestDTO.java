package com.webatrio.eventsmanager.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserRegisterRequestDTO {

    private String username;

    private String password;

    private String entityNo;

    private String firstname;

    private String lastname;

    private String initial;

    private String idNumber;

    private Date startDate;

    private Date endDate;

    private String email;

    private String mobile;

    private List<String> roleList = new ArrayList<>();
}
