package com.scplatform.api.controller;

import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PasswordManagementController {

    @Autowired private UsersRepository usersRepository;
    @Autowired private BCryptPasswordEncoder bcrypt;

    @PostMapping("/auth/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(value="X-User-Id", defaultValue="") String userId,
            @RequestBody ChangePasswordRequest req) {
        if (req.newPassword() == null || req.newPassword().length() < 6)
            return ResponseEntity.badRequest().body(Map.of("error","Password must be at least 6 characters"));
        List<Users> users = usersRepository.findAllByUserId(userId);
        if (users.size() != 1) return ResponseEntity.status(404).body(Map.of("error","User not found"));
        Users user = users.get(0);
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            if (req.currentPassword() == null || !bcrypt.matches(req.currentPassword(), user.getPassword()))
                return ResponseEntity.status(401).body(Map.of("error","Current password is incorrect"));
        }
        user.setPassword(bcrypt.encode(req.newPassword()));
        usersRepository.save(user);
        return ResponseEntity.ok(Map.of("message","Password updated successfully"));
    }

    @PostMapping("/admin/users/{userId}/set-password")
    public ResponseEntity<?> adminSetPassword(@PathVariable String userId, @RequestBody AdminSetPasswordRequest req) {
        if (req.newPassword() == null || req.newPassword().length() < 6)
            return ResponseEntity.badRequest().body(Map.of("error","Password must be at least 6 characters"));
        List<Users> users = usersRepository.findAllByUserId(userId);
        if (users.size() != 1) return ResponseEntity.status(404).body(Map.of("error","User not found: " + userId));
        users.get(0).setPassword(bcrypt.encode(req.newPassword()));
        usersRepository.save(users.get(0));
        return ResponseEntity.ok(Map.of("message","Password set for: " + userId));
    }

    @PostMapping("/admin/users/{userId}/reset-password")
    public ResponseEntity<?> adminResetPassword(@PathVariable String userId) {
        List<Users> users = usersRepository.findAllByUserId(userId);
        if (users.size() != 1) return ResponseEntity.status(404).body(Map.of("error","User not found: " + userId));
        users.get(0).setPassword(null);
        usersRepository.save(users.get(0));
        return ResponseEntity.ok(Map.of("message","Password reset - any password accepted on next login"));
    }

    record ChangePasswordRequest(String currentPassword, String newPassword) {}
    record AdminSetPasswordRequest(String newPassword) {}
}