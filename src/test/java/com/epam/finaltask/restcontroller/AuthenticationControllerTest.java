package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.LoginRequestDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.JwtService;
import com.epam.finaltask.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthenticationControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void showRegisterForm_ShouldAddUserToModelAndReturnView() {
        String view = authController.showRegisterForm(model);
        assertEquals("auth/register", view);
        verify(model, times(1)).addAttribute(eq("user"), any(UserDTO.class));
    }

    @Test
    void registerUser_WithErrors_ShouldReturnRegisterView() {
        when(bindingResult.hasErrors()).thenReturn(true);
        UserDTO userDTO = new UserDTO();

        String view = authController.registerUser(userDTO, bindingResult, model);

        assertEquals("auth/register", view);
        verify(userService, never()).register(any());
    }

    @Test
    void registerUser_Success_ShouldRedirectToLogin() {
        when(bindingResult.hasErrors()).thenReturn(false);
        UserDTO userDTO = new UserDTO();

        String view = authController.registerUser(userDTO, bindingResult, model);

        assertEquals("redirect:/login", view);
        verify(userService, times(1)).register(userDTO);
    }

    @Test
    void login_UserInactive_ShouldReturnSignInWithError() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("pass");

        UserDTO user = new UserDTO();
        user.setUsername("testuser");
        user.setActive(false);

        when(userService.getUserByUsername("testuser")).thenReturn(user);

        String view = authController.login(loginRequest, response, model);

        assertEquals("auth/sign-in", view);
        verify(model).addAttribute(eq("error"), anyString());
        verify(model).addAttribute("loginRequest", loginRequest);
    }

    @Test
    void login_InvalidPassword_ShouldReturnSignInWithError() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpass");

        UserDTO user = new UserDTO();
        user.setUsername("testuser");
        user.setActive(true);
        user.setPassword("encodedpass");

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpass", "encodedpass")).thenReturn(false);

        String view = authController.login(loginRequest, response, model);

        assertEquals("auth/sign-in", view);
        verify(model).addAttribute(eq("error"), anyString());
        verify(model).addAttribute("loginRequest", loginRequest);
    }

    @Test
    void login_ValidCredentials_ShouldRedirectToVouchers() {
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("pass");

        UserDTO user = new UserDTO();
        user.setUsername("testuser");
        user.setActive(true);
        user.setPassword("encodedpass");
        user.setRole(null);

        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("pass", "encodedpass")).thenReturn(true);
        when(jwtService.generateToken("testuser", null)).thenReturn("jwt-token");

        String view = authController.login(loginRequest, response, model);

        assertEquals("redirect:/vouchers", view);
        verify(response).addCookie(any(Cookie.class));
    }

    @Test
    void logout_ShouldClearJwtAndRedirect() throws Exception {
        AuthenticationController controllerSpy = Mockito.spy(authController);

        doNothing().when(response).addHeader(anyString(), anyString());
        doNothing().when(response).sendRedirect("/login");

        controllerSpy.logout(response);

        verify(response, times(1)).addHeader(eq("Set-Cookie"), contains("jwt="));
        verify(response, times(1)).sendRedirect("/login");
    }
}