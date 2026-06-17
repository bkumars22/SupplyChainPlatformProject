package com.scplatform.api.controller;

import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.role.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class UserManagementController {

    @Autowired private UsersRepository usersRepository;
    @Autowired private RoleRepository roleRepository;
    @Autowired private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder bcrypt;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        List<Map<String,Object>> result = usersRepository.findAll().stream()
                .map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("users", result, "total", result.size()));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        List<Users> users = usersRepository.findAllByUserId(userId);
        if (users.isEmpty()) return ResponseEntity.status(404).body(Map.of("error","User not found: " + userId));
        return ResponseEntity.ok(toDto(users.get(0)));
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest req) {
        if (req.userId() == null || req.userId().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error","userId is required"));
        if (!usersRepository.findAllByUserId(req.userId()).isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error","User already exists: " + req.userId()));
        Role role = resolveRole(req.roleName());
        if (role == null) return ResponseEntity.badRequest().body(Map.of("error","Invalid role: " + req.roleName()));
        Users u = new Users();
        u.setUserId(req.userId());
        u.setUserName(req.userName());
        u.setRole(role);
        u.setIsEnabled(true);
        if (req.password() != null && !req.password().isBlank()) u.setPassword(bcrypt.encode(req.password()));
        usersRepository.save(u);
        return ResponseEntity.status(201).body(Map.of("message","User created","userId",u.getUserId()));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UpdateUserRequest req) {
        List<Users> users = usersRepository.findAllByUserId(userId);
        if (users.isEmpty()) return ResponseEntity.status(404).body(Map.of("error","User not found: " + userId));
        Users u = users.get(0);
        if (req.userName()  != null) u.setUserName(req.userName());
        if (req.isEnabled() != null) u.setIsEnabled(req.isEnabled());
        if (req.roleName()  != null) {
            Role r = resolveRole(req.roleName());
            if (r == null) return ResponseEntity.badRequest().body(Map.of("error","Invalid role: " + req.roleName()));
            u.setRole(r);
        }
        usersRepository.save(u);
        return ResponseEntity.ok(Map.of("message","User updated","userId",userId));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> disableUser(@PathVariable String userId) {
        List<Users> users = usersRepository.findAllByUserId(userId);
        if (users.isEmpty()) return ResponseEntity.status(404).body(Map.of("error","User not found: " + userId));
        Users target = users.get(0);
        target.setIsEnabled(false);
        usersRepository.save(target);
        return ResponseEntity.ok(Map.of("message","User disabled","userId",userId));
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getRoles() {
        List<Map<String,Object>> roles = roleRepository.findAll().stream().map(r -> {
            Map<String,Object> m = new HashMap<>();
            m.put("roleName", r.getRoleName() != null ? r.getRoleName() : "");
            try { m.put("roleKey", r.getRoleKey()); } catch(Exception ignored) {}
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("roles", roles));
    }

    private Map<String,Object> toDto(Users u) {
        Map<String,Object> m = new HashMap<>();
        m.put("userId",    u.getUserId()   != null ? u.getUserId()   : "");
        m.put("userName",  u.getUserName() != null ? u.getUserName() : "");
        m.put("emailId",   "");
        m.put("isEnabled", u.getIsEnabled());
        m.put("hasPassword", u.getPassword() != null && !u.getPassword().isBlank());
        try { m.put("roleName", u.getRole() != null ? u.getRole().getRoleName() : ""); } catch(Exception e) { m.put("roleName",""); }
        return m;
    }

    private Role resolveRole(String name) {
        if (name == null) return null;
        return roleRepository.findAll().stream()
                .filter(r -> name.equalsIgnoreCase(r.getRoleName()))
                .findFirst().orElse(null);
    }

    record CreateUserRequest(String userId, String userName, String roleName, String password) {}
    record UpdateUserRequest(String userName, String roleName, Boolean isEnabled) {}
}