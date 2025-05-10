package com.resumereviewer.webapp.controller;

import com.resumereviewer.webapp.dto.request.GoogleAuthRequest;
import com.resumereviewer.webapp.dto.request.LoginDTO;
import com.resumereviewer.webapp.dto.request.RegisterDTO;
import com.resumereviewer.webapp.dto.response.LoginResponse;
import com.resumereviewer.webapp.dto.response.Status;
import com.resumereviewer.webapp.entity.UserEntity;
import com.resumereviewer.webapp.repository.UserEntityRepository;
import com.resumereviewer.webapp.service.KeyManagementService;
import com.resumereviewer.webapp.service.UserService;
import com.resumereviewer.webapp.util.JWTUtil;
import com.resumereviewer.webapp.util.PasswordGenerator;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JWTUtil jwtUtil;

    @Autowired
    KeyManagementService keyManagementService;
    @Autowired
    UserEntityRepository userRepository;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            String decryptedPassword = keyManagementService.decryptPassword(loginDTO.getPassword());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), decryptedPassword)
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus .UNAUTHORIZED)
                    .body(new LoginResponse(null, "FAIL", "Invalid username or password", null, null));
        }


        final UserDetails userDetails = userService.loadUserByUsername(loginDTO.getUsername());
        String username = userDetails.getUsername();
        UserEntity userEntity = userService.findByUsername(username);
        String firstname = userEntity.getFirstname();

        /** Handle admin login */
        if(BooleanUtils.isTrue(loginDTO.getAdminLogin())) {
            System.out.println("USER ROLES: " + userEntity.getRoles().stream().map(a -> a.getName()).toList());
            if (userEntity.getRoles().stream().filter(a -> a.getName().equalsIgnoreCase("ADMIN")).count() == 0) {
                return ResponseEntity.badRequest().body(new LoginResponse(null, "FAIL", "Admin login failed", null, null));
            }
        }

        final String token = jwtUtil.generateToken(userDetails.getUsername()).trim(); // Ensure no whitespace

        return ResponseEntity.ok(new LoginResponse(token, "SUCCESS", "Token generated successfully",
                username, firstname));
    }

    @PostMapping("/google-login")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody GoogleAuthRequest googleAuthRequest) {
        try {
            // 1. Validate Firebase token (optional but recommended)
            // If you want to verify the token with Firebase, you can add Firebase Admin SDK

            // 2. Check if user exists by firebaseUid
            Optional<UserEntity> existingUser = userRepository.findByFirebaseUid(googleAuthRequest.getFirebaseUid());
            UserEntity user;

            if (existingUser.isPresent()) {
                // User exists - use existing user
                user = existingUser.get();
            } else {
                // User doesn't exist - create new user

                String firebaseUid = googleAuthRequest.getFirebaseUid();
                String email = googleAuthRequest.getEmail();

                // Generate a unique username (could be from email or display name)
                String baseUsername = googleAuthRequest.getEmail().split("@")[0];
                String username = generateUniqueUsername(baseUsername);

                // Set names from display name
                String[] nameParts = googleAuthRequest.getDisplayName().split(" ", 2);
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                RegisterDTO registerDTO = RegisterDTO.builder()
                        .username(username)
                        .firstname(firstName)
                        .lastname(lastName)
                        .firebaseUid(firebaseUid)
                        .email(email)
                        .password(PasswordGenerator.generateRandomPassword(10))
                        .build();

                Status status = userService.register(registerDTO);
                if (!status.getStatus().equals("SUCCESS")) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(new LoginResponse(null, "FAIL", "Google authentication failed: " + status.getMessage(), null, null));
                }

                user = userRepository.findByUsername(username);
            }

            if(user != null) {
                // 3. Generate JWT token (same as password login)
                final String token = jwtUtil.generateToken(user.getUsername()).trim();

                // 4. Return the same response structure as password login
                return ResponseEntity.ok(
                        new LoginResponse(token, "SUCCESS", "Google authentication successful",
                                user.getUsername(), user.getFirstname()));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(null, "FAIL", "Google authentication failed: User not found", null, null));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, "FAIL", "Google authentication failed: " + e.getMessage(), null, null));
        }
    }

    /** Helper method to generate unique username */
    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        return username;
    }

}
