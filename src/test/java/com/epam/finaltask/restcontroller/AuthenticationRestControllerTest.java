package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationRestControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthenticationRestController authenticationRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldReturnCreatedUser() {
        // given
        UserDTO requestDTO = new UserDTO();
        requestDTO.setUsername("testuser");
        requestDTO.setPassword("password");

        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(UUID.randomUUID().toString());
        responseDTO.setUsername("testuser");

        when(userService.register(ArgumentMatchers.any(UserDTO.class))).thenReturn(responseDTO);

        // when
        ResponseEntity<UserDTO> response = authenticationRestController.register(requestDTO);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(responseDTO, response.getBody());
        verify(userService, times(1)).register(requestDTO);
    }

    @Test
    void register_ShouldCallUserServiceOnce() {
        UserDTO requestDTO = new UserDTO();
        requestDTO.setUsername("user");
        requestDTO.setPassword("pass");

        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(UUID.randomUUID().toString());
        responseDTO.setUsername("user");

        when(userService.register(any(UserDTO.class))).thenReturn(responseDTO);

        authenticationRestController.register(requestDTO);

        verify(userService, times(1)).register(requestDTO);
    }
}