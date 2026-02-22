package com.epam.finaltask.repository;

import com.epam.finaltask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findUserByUsername(String username);

  Boolean existsByUsername(String username);

  Boolean existsByPhoneNumber(String phoneNumber);
}
