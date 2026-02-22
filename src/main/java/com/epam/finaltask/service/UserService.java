package com.epam.finaltask.service;

import java.util.List;
import java.util.UUID;

import com.epam.finaltask.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDTO register(UserDTO userDTO);

    UserDTO updateUser(String username, UserDTO userDTO);

    UserDTO getUserByUsername(String username);
    UserDTO changeAccountStatus(UserDTO userDTO);
    UserDTO getUserById(UUID id);
    List<UserDTO> findAll();
    Page<UserDTO> findAll(Pageable pageable);
    Page<UserDTO> searchByUsername(String username, Pageable pageable);
    UserDTO changeUserRole(UUID userId, String newRole);
}
