package com.test.demo.service;

import com.test.demo.dto.UserResponse;
import com.test.demo.model.UserEntity;
import com.test.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserEntity> getUsersWithOwnerRolePaged(Pageable pageable) {
        return userRepository.findByRolesName("owner", pageable);
    }


    public Page<UserEntity> getUsersWithRenterRolePaged(Pageable pageable) {
        return userRepository.findByRolesName("rentee", pageable);
    }

    public long getTotalOwners() {
        return userRepository.countByRolesName("owner");
    }

    public long getTotalRenters() {
        return userRepository.countByRolesName("rentee");
    }

    public Page<UserEntity> getUsersWithSubAdminsRolePaged(Pageable pageable) {
        return userRepository.findByRolesName("sub_admin", pageable);
    }
}
