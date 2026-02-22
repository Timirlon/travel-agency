package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserDTO userDTO) {

        Role role = userDTO.getRole() != null ? Role.valueOf(userDTO.getRole()) : null;

        return User.builder()
                .id(userDTO.getId() != null ? (userDTO.getId()) : null)
                .username(userDTO.getUsername())
                .password(userDTO.getPassword())
                .role(role)
                .active(userDTO.isActive())
                .balance(userDTO.getBalance())
                .phoneNumber(userDTO.getPhoneNumber())
                .build();
    }

    public UserDTO toUserDTO(User userModel) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userModel.getId() != null ? userModel.getId() : null);
        userDTO.setUsername(userModel.getUsername());
        userDTO.setPassword(userModel.getPassword());
        userDTO.setRole(userModel.getRole() != null ? userModel.getRole().name() : null);
        userDTO.setActive(userModel.isActive());
        userDTO.setBalance(userModel.getBalance());
        userDTO.setPhoneNumber(userModel.getPhoneNumber());
        return userDTO;
    }
}
