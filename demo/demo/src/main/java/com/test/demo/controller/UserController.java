package com.test.demo.controller;
import com.test.demo.dto.RegisterDto;
import com.test.demo.dto.UserDto;
import com.test.demo.model.Role;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.Collections;
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
    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
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


    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (optionalUser.isPresent()) {
            userRepository.delete(optionalUser.get());
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





