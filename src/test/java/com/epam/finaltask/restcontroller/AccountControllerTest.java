package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.security.Principal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showAccountPage_ShouldReturnProfileViewAndAddUserToModel() {
        // given
        String username = "testuser";
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(username);

        when(principal.getName()).thenReturn(username);
        when(userService.getUserByUsername(username)).thenReturn(userDTO);

        // when
        String viewName = accountController.showAccountPage(model, principal);

        // then
        assertEquals("account/profile", viewName);
        verify(userService, times(1)).getUserByUsername(username);
        verify(model, times(1)).addAttribute("user", userDTO);
    }
}