package com.test.demo.controller;
import com.test.demo.dto.UserDto;
import com.test.demo.dto.UserResponse;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.UserRepository;

import com.test.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000")
@Transactional
@RestController
@RequestMapping("/api/users")

public class UserController {

   private PasswordEncoder passwordEncoder;


    private UserRepository userRepository;

    private UserService userService;

    @Autowired
    public UserController(PasswordEncoder passwordEncoder, UserRepository userRepository, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Operation(summary = "this is api to fetch all the users from database")
    @GetMapping("/view-users")
    public UserResponse getAllUsers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam int pageSize) {
            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<UserEntity> users = userRepository.findAll(pageable);

            List<UserEntity> listOfUsers = users.getContent();
            UserResponse userResponse = new UserResponse();
            userResponse.setContent(listOfUsers);
            userResponse.setPageNo(pageNo); // Set the page number from the request
            userResponse.setPageSize(pageSize); // Set the page size from the request
            userResponse.setTotalElements(users.getTotalElements());
            userResponse.setTotalPages(users.getTotalPages());
            userResponse.setLast(users.isLast());

            return userResponse;
        }

    @GetMapping("/owners")
    public ResponseEntity<UserResponse> getUsersWithOwnerRole(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> ownerUsers = userService.getUsersWithOwnerRolePaged(pageable);

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(ownerUsers.getContent());
        userResponse.setPageNo(ownerUsers.getNumber());
        userResponse.setPageSize(ownerUsers.getSize());
        userResponse.setTotalElements(ownerUsers.getTotalElements());
        userResponse.setTotalPages(ownerUsers.getTotalPages());
        userResponse.setLast(ownerUsers.isLast());

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/renters")
    public ResponseEntity<UserResponse> getUsersWithRenterRole(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEntity> ownerUsers = userService.getUsersWithRenterRolePaged(pageable);

        UserResponse userResponse = new UserResponse();
        userResponse.setContent(ownerUsers.getContent());
        userResponse.setPageNo(ownerUsers.getNumber());
        userResponse.setPageSize(ownerUsers.getSize());
        userResponse.setTotalElements(ownerUsers.getTotalElements());
        userResponse.setTotalPages(ownerUsers.getTotalPages());
        userResponse.setLast(ownerUsers.isLast());

        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/owners/count")
    public ResponseEntity<Long> getTotalOwnersCount() {
        long ownerCount = userService.getTotalOwners();
        return ResponseEntity.ok(ownerCount);
    }

    @GetMapping("/renters/count")
    public ResponseEntity<Long> getTotalRentersCount() {
        long renterCount = userService.getTotalRenters();
        return ResponseEntity.ok(renterCount);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/Profile")
    public ResponseEntity<UserEntity> userProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            String username = userDetails.getUsername(); // Get the currently logged-in username
            Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();

                // Return the user details for the front-end to populate the update form
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<String> updateUser(@AuthenticationPrincipal UserDetails userDetails,
                                             @RequestBody UserDto userDto) {
        if (userDetails != null) {
            String username = userDetails.getUsername(); // Get the currently logged-in username
            Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isPresent()) {
                UserEntity user = optionalUser.get();

                // Check if the updated username already exists in the database
                if (!userDto.getUsername().equals(username) &&
                        userRepository.existsByUsername(userDto.getUsername())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already exists.");
                }

                // Update user data based on userUpdateDto
                user.setUsername(userDto.getUsername());
                user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                user.setEmail(userDto.getEmail());

                userRepository.save(user);

                return ResponseEntity.ok("User updated successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated.");
        }
    }



    @PutMapping("/{username}/updateStatus")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable String username,
            @RequestBody UserDto userDto
    ) {

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setActive(userDto.isActive());
        userRepository.save(user);
        return ResponseEntity.ok("User status updated");
    }

}





