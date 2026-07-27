package com.scplatform.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * The web frontend (api.js and UsersPage.js) was built against /api/users
 * and /api/roles, while UserManagementController/PasswordManagementController
 * expose the same operations under /api/admin/*. Rather than move the
 * already-tested /api/admin/* routes, this delegates the frontend-expected
 * paths to the same controller methods so both surfaces stay in sync.
 */
@RestController
public class UsersApiController {

    @Autowired private UserManagementController userManagementController;
    @Autowired private PasswordManagementController passwordManagementController;

    @GetMapping("/api/users")
    public ResponseEntity<?> getAllUsers() {
        return userManagementController.getAllUsers();
    }

    @GetMapping("/api/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        return userManagementController.getUser(userId);
    }

    @PostMapping("/api/users")
    public ResponseEntity<?> createUser(@RequestBody UserManagementController.CreateUserRequest req) {
        return userManagementController.createUser(req);
    }

    @PutMapping("/api/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserManagementController.UpdateUserRequest req) {
        return userManagementController.updateUser(userId, req);
    }

    @PutMapping("/api/users/{userId}/disable")
    public ResponseEntity<?> disableUser(@PathVariable String userId) {
        return userManagementController.disableUser(userId);
    }

    @PutMapping("/api/users/{userId}/password")
    public ResponseEntity<?> setPassword(@PathVariable String userId, @RequestBody PasswordManagementController.AdminSetPasswordRequest req) {
        return passwordManagementController.adminSetPassword(userId, req);
    }

    @GetMapping("/api/roles")
    public ResponseEntity<?> getRoles() {
        return userManagementController.getRoles();
    }
}
