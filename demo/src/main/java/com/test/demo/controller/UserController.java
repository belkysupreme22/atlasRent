package com.test.demo.controller;
import com.test.demo.dto.UserDto;
import com.test.demo.dto.UserResponse;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
@Transactional
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
   private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/view-users")
    public UserResponse getAllUsers(@RequestParam int pageNo, @RequestParam int pageSize) {
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

    @PutMapping("/updateProfile/{username}")
    public ResponseEntity<String> updateUser(@PathVariable String username,
                                             @RequestBody UserDto userDto) {
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
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        Optional<UserEntity> optionalUser = userRepository.findById(id);

        if (optionalUser.isPresent()) {
            userRepository.deleteById(id);
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
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





