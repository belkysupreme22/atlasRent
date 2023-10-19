package com.test.demo.controller;
import com.test.demo.dto.AuthResponseDTO;
import com.test.demo.dto.ChangePasswordDto;
import com.test.demo.dto.LoginDto;
import com.test.demo.dto.RegisterDto;
import com.test.demo.model.Role;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.RoleRepository;
import com.test.demo.repository.UserRepository;

import com.test.demo.security.CustomUserDetailsService;
import com.test.demo.security.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Optional;

@CrossOrigin(origins = "localhost:3000")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()));

        var user = userRepository.findByUsername(loginDto.getUsername()).orElseThrow();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var token = jwtService.generateToken(user);

        //validation check
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUsername());

        if(userDetails == null && passwordEncoder.matches(loginDto.getPassword(), user.getPassword())){
            throw new BadCredentialsException("invalid username or password");
        }

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setAccessToken(token);
        authResponseDTO.setMessage("loggedIn successfully");

        return new ResponseEntity<AuthResponseDTO>(authResponseDTO,HttpStatus.CREATED);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());

        // Determine the role based on the user's preference
        Role role;
        switch (registerDto.getRole().toUpperCase()) {
            case "SUB_ADMIN":
                role = roleRepository.findByName("SUB_ADMIN").orElse(null);
                break;
            case "RENTEE":
                role = roleRepository.findByName("RENTEE").orElse(null);
                break;
            case "OWNER":
                role = roleRepository.findByName("OWNER").orElse(null);
                break;
            default:
                role = null;
                break;
        }

        if (role == null) {
            return new ResponseEntity<>("Invalid role preference!", HttpStatus.BAD_REQUEST);
        }

        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto){
        Optional<UserEntity> optionalUser= userRepository.findByUsername(changePasswordDto.getUsername());
        if (optionalUser.isPresent()){
            UserEntity user = optionalUser.get();
        try{
                if(changePasswordDto.getNewPassword().equals(changePasswordDto.getOldPassword())){
                throw new RuntimeException("old and new password can't match");
            }
                if(!passwordEncoder.matches(changePasswordDto.getOldPassword(),user.getPassword())){
                throw new RuntimeException("the password you entered is wrong");
            }
            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword()));
            userRepository.save(user);

            return new ResponseEntity<>("password changed successfully", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("failed to change password: "+e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }
}
