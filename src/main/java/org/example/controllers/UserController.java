package org.example.controllers;

import jakarta.transaction.Transactional;
 import org.example.models.Role;
import org.example.models.RoleEnum;
import org.example.models.User;
import org.example.payload.request.LoginRequest;
import org.example.payload.request.SignupRequest;
import org.example.payload.response.JwtResponse;
import org.example.payload.response.MessageResponse;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;



  @PostMapping("/signin")
  public String signIn(String username, String password) {
    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    if (user.getPassword().equals(password)) {
      return null;
    } else {
      throw new IllegalArgumentException("Invalid credentials");
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Username is already taken!"));
    }

    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is already in use!"));
    }

    User user = new User(signUpRequest.getUsername(),
               signUpRequest.getEmail(),
               null);

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    if (strRoles == null) {
      Role userRole = roleRepository.findByName(RoleEnum.client)
          .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
      roles.add(userRole);
    } else {
      strRoles.forEach(role -> {
        switch (role) {
        case "admin":
          Role adminRole = roleRepository.findByName(RoleEnum.admin)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(adminRole);

          break;
        case "dealer":
          Role modRole = roleRepository.findByName(RoleEnum.dealer)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(modRole);

          break;
        default:
          Role userRole = roleRepository.findByName(RoleEnum.client)
              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          roles.add(userRole);
        }
      });
    }

    user.setRoles(roles);
    userRepository.save(user);

    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
  }


  @PostMapping("/users/create")
  public User createUser(@RequestBody LoginRequest loginRequest) {
      if (userRepository.findByUsername(loginRequest.getUsername()) != null) {
        throw new IllegalArgumentException("Username already exists");
      }
      if (userRepository.existsByEmail(loginRequest.getEmail()) != null) {
        throw new IllegalArgumentException("Email already exists");
      }

      String encodedPassword = null;

      User user = new User();
      user.setUsername(loginRequest.getUsername());
      user.setPassword(encodedPassword);
      user.setEmail(loginRequest.getEmail());

      return userRepository.save(user);


  }


  @PostMapping("/users/{id}/change-status")
  public ResponseEntity<?> activeUser(@PathVariable Long id, @RequestParam boolean active) {
    try {
      User updatedUser = activateOrDeactivateUser(id,
              active);
      return ResponseEntity.ok(updatedUser);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    }
 }

  @Transactional
  public User activateOrDeactivateUser(Long userId, boolean active) {
    String currentUsername = null;

    Optional<User> admin = userRepository.findByUsername(currentUsername);
    if (admin == null || admin.get().getRoles().equals("admin") &&
            admin.get().getUsername().equals(userId)) {
      throw new IllegalArgumentException("An admin cannot deactivate themselves");
    }

    // Get the user to activate or deactivate
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    user.setActive(active);
    return userRepository.save(user);
  }


}

