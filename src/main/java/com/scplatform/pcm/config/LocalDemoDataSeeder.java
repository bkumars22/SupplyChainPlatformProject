/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.config;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.role.repository.RoleRepository;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class LocalDemoDataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(LocalDemoDataSeeder.class);

    private final UsersRepository usersRepository;
    private final RoleRepository roleRepository;

    public LocalDemoDataSeeder(UsersRepository usersRepository, RoleRepository roleRepository) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedLocalDemoUser() {
        if (usersRepository.findByUserId("kumar").isPresent()) {
            logger.info("Local demo user 'kumar' already exists.");
            return;
        }

        Role role = roleRepository.findByRoleIdIgnoreCase("ADMIN")
                .orElseGet(this::createDefaultAdminRole);

        Users localUser = new Users();
        localUser.setUserId("kumar");
        localUser.setUserName("Kumar Local Demo");
        localUser.setRole(role);
        localUser.setIsEnabled(true);
        usersRepository.save(localUser);

        logger.info("Created local demo user 'kumar' with role '{}' for the learning project.", role.getRoleId());
    }

    private Role createDefaultAdminRole() {
        Role role = new Role();
        role.setRoleId("ADMIN");
        role.setRoleName("Administrator");
        role.setPermRole(true);
        return roleRepository.save(role);
    }
}
