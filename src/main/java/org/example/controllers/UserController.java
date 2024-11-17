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

import org.example.security.jwt.JwtUtils;
import org.example.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class UserController {
// the @Autowired annotation tells Spring to automatically inject an instance of the UserRepository into this controller.
  @Autowired
  UserRepository userRepository;

  @Autowired
  RoleRepository roleRepository;

  @Autowired
  JwtUtils jwtUtils;

  @PostMapping("/signin")
  public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
    // Find user by username
    User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    List<User> usaser = userRepository.findAll();


    // Check if the password matches (this can be improved with hashing)
    if (user.getPassword().equals(loginRequest.getPassword())) {

      return ResponseEntity
              .ok()
              .body(new JwtResponse(jwtUtils.generateToken(loginRequest.getUsername())));


    } else {
      throw new IllegalArgumentException("Invalid credentials");
    }
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
    try {
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

      // Create new user's account
      User user = new User(signUpRequest.getUsername(),
              signUpRequest.getEmail(),
              signUpRequest.getPassword());

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
              Role dealerRole = roleRepository.findByName(RoleEnum.dealer)
                      .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
              roles.add(dealerRole);

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
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null);
    }

  }


  @PostMapping("/users/create")
  @PreAuthorize("hasRole('admin')")
  public ResponseEntity<?> createUser(@RequestBody SignupRequest signUpRequest) {
    // Check if username or email already exists
//    if (userRepository.findByUsername(signUpRequest.getUsername()) != null) {
//       return ResponseEntity.ok("Username already exists");
//    }
//    if (userRepository.existsByEmail(signUpRequest.getEmail()) != null) {
//      throw new IllegalArgumentException("Email already exists");
//    }

    // Encode the password
    String encodedPassword = null;
    User user = new User(signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            signUpRequest.getPassword());
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
            Role dealerRole = roleRepository.findByName(RoleEnum.dealer)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(dealerRole);

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

    Optional<User> admin = userRepository.findById(userId);
//    if (admin == null || admin.get().getRoles().equals("admin") &&
//            admin.get().getUsername().equals(userId)) {
//      throw new IllegalArgumentException("An admin cannot deactivate themselves");
//    }

    // Get the user to activate or deactivate
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    user.setActive(active);
    return userRepository.save(user);
  }


}

