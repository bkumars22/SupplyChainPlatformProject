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
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds the DEMO role and demo@scip.com user on startup if they do not exist.
 * Demo user: GET-only, no write access, password fixed (cannot be changed via API).
 */
@Component
@Order(10)
public class DemoUserSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoUserSeeder.class);
    private static final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    static final String DEMO_ROLE_ID   = "DEMO";
    static final String DEMO_USER_ID   = "demo";
    static final String DEMO_EMAIL     = "demo@scip.com";
    static final String DEMO_PASSWORD  = "Demo@2026";

    private final RoleRepository roleRepo;
    private final UsersRepository usersRepo;

    public DemoUserSeeder(RoleRepository roleRepo, UsersRepository usersRepo) {
        this.roleRepo  = roleRepo;
        this.usersRepo = usersRepo;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Role demoRole = roleRepo.findRoleById(DEMO_ROLE_ID);
            if (demoRole == null) {
                demoRole = new Role();
                demoRole.setRoleId(DEMO_ROLE_ID);
                demoRole.setRoleName("Demo User");
                demoRole.setPermRole(false);
                roleRepo.save(demoRole);
                log.info("DemoUserSeeder: created DEMO role");
            }

            if (usersRepo.findAllByUserId(DEMO_USER_ID).isEmpty()) {
                Users demo = new Users();
                demo.setUserId(DEMO_USER_ID);
                demo.setUserName("Demo User");
                demo.setEmailAddress(DEMO_EMAIL);
                demo.setPassword(bcrypt.encode(DEMO_PASSWORD));
                demo.setIsEnabled(true);
                demo.setRole(demoRole);
                usersRepo.save(demo);
                log.info("DemoUserSeeder: created demo user ({})", DEMO_EMAIL);
            }
        } catch (Exception e) {
            log.warn("DemoUserSeeder skipped: {}", e.getMessage());
        }
    }
}
