package com.webatrio.eventsmanager.repository;

import com.webatrio.eventsmanager.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUserRoleRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findAllByUserId(long id);
}
