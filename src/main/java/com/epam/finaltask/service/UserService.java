package com.epam.finaltask.service;

import com.epam.finaltask.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    UserDTO register(UserDTO userDTO);

    UserDTO updateBalance(String username, double amount);
    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    Page<UserDTO> getAllUsers(Pageable pageable);
    UserDTO getUserById(UUID id);
}
