package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserRestController userRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getByUsername_ShouldReturnUser() {
        String username = "testuser";
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);

        when(userService.getUserByUsername(username)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userRestController.getByUsername(username);

        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        String username = "testuser";
        UserDTO requestDTO = new UserDTO();
        requestDTO.setUsername("newName");

        UserDTO responseDTO = new UserDTO();
        responseDTO.setUsername("newName");

        when(userService.updateUser(eq(username), any(UserDTO.class))).thenReturn(responseDTO);

        ResponseEntity<UserDTO> response = userRestController.updateUser(username, requestDTO);

        assertEquals(responseDTO, response.getBody());
        verify(userService, times(1)).updateUser(username, requestDTO);
    }

    @Test
    void changeStatus_ShouldReturnUpdatedUser() {
        UserDTO requestDTO = new UserDTO();
        requestDTO.setActive(true);

        UserDTO responseDTO = new UserDTO();
        responseDTO.setActive(true);

        when(userService.changeAccountStatus(any(UserDTO.class))).thenReturn(responseDTO);

        ResponseEntity<UserDTO> response = userRestController.changeStatus(requestDTO);

        assertEquals(responseDTO, response.getBody());
        verify(userService, times(1)).changeAccountStatus(requestDTO);
    }

    @Test
    void getById_ShouldReturnUser() {
        UUID id = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id.toString());

        when(userService.getUserById(id)).thenReturn(userDTO);

        ResponseEntity<UserDTO> response = userRestController.getById(id);

        assertEquals(userDTO, response.getBody());
        verify(userService, times(1)).getUserById(id);
    }
}
