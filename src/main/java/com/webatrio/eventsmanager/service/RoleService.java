package com.webatrio.eventsmanager.service;

import com.webatrio.eventsmanager.entity.Role;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.webatrio.eventsmanager.repository.IRoleRepository;

import java.util.List;


@Service
@Transactional
public class RoleService {
    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private IRoleRepository roleRepository;

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> findAllRole() {
        return roleRepository.findAll();
    }

    public Role findDefaultRole() {
        return findAllRole().stream().findFirst().orElse(null);
    }

    public Role findRoleByName(String role) {
        return findAllRole().stream().filter(r -> r.getName().equals(role)).findFirst().orElse(null);
    }
}
