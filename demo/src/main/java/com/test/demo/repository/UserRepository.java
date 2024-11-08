package com.test.demo.repository;


import com.test.demo.dto.UserDto;
import com.test.demo.model.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);

    Page<UserEntity> findByRolesName(String roleName, Pageable pageable);

    long countByRolesName(String roleName);

}
