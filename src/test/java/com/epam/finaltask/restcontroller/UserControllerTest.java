package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllUsers_NoUsername_ShouldReturnUsersView() {
        UserDTO user1 = new UserDTO();
        user1.setUsername("user1");
        Page<UserDTO> page = new PageImpl<>(List.of(user1));

        when(userService.findAll(any(Pageable.class))).thenReturn(page);

        String view = userController.getAllUsers(null, 0, 10, model);

        assertEquals("user/users", view);
        verify(model).addAttribute("users", page.getContent());
        verify(model).addAttribute("page", page);
    }

    @Test
    void getAllUsers_WithUsername_ShouldSearchAndReturnUsersView() {
        String search = "john";
        UserDTO user1 = new UserDTO();
        user1.setUsername(search);
        Page<UserDTO> page = new PageImpl<>(List.of(user1));

        when(userService.searchByUsername(eq(search), any(Pageable.class))).thenReturn(page);

        String view = userController.getAllUsers(search, 0, 10, model);

        assertEquals("user/users", view);
        verify(model).addAttribute("users", page.getContent());
        verify(model).addAttribute("page", page);
        verify(model).addAttribute("searchedUsername", search);
    }

    @Test
    void changeUserRole_Success_ShouldReturnOk() {
        UUID id = UUID.randomUUID();
        assertEquals(200, userController.changeUserRole(id, "ADMIN").getStatusCodeValue());
        verify(userService, times(1)).changeUserRole(id, "ADMIN");
    }

    @Test
    void changeUserStatus_Success_ShouldReturnOk() {
        UUID id = UUID.randomUUID();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id.toString());
        userDTO.setActive(false);

        when(userService.getUserById(id)).thenReturn(userDTO);

        assertEquals(200, userController.changeUserStatus(id, true).getStatusCodeValue());
        verify(userService).changeAccountStatus(userDTO);
    }

    @Test
    void updateUser_Success_ShouldRedirect() {
        UUID id = UUID.randomUUID();
        String role = "ADMIN";

        when(redirectAttributes.addFlashAttribute(anyString(), anyString())).thenReturn(redirectAttributes);

        String view = userController.updateUser(id, role, "on", redirectAttributes);

        assertEquals("redirect:/users", view);
        verify(userService).changeUserRole(id, role);
        verify(userService).changeAccountStatus(any(UserDTO.class));
        verify(redirectAttributes).addFlashAttribute("success", "User updated successfully.");
    }

    @Test
    void updateUser_Failure_ShouldRedirectWithError() {
        UUID id = UUID.randomUUID();
        String role = "ADMIN";

        doThrow(new RuntimeException()).when(userService).changeUserRole(id, role);

        String view = userController.updateUser(id, role, "on", redirectAttributes);

        assertEquals("redirect:/users", view);
        verify(redirectAttributes).addFlashAttribute("error", "Failed to update user.");
    }
}