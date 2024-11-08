package com.test.demo.security;

import com.test.demo.model.Role;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

 private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        UserEntity user=userRepository.findByUsername(username).
                orElseThrow(() -> new UsernameNotFoundException("username not found"));
        return new User(user.getUsername(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }
private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList());
}

}
