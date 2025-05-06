package com.domulink.repository;

import com.domulink.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User>  findUserByEmail(String email);
    Optional<User> findByUuid(String uuid);
}
