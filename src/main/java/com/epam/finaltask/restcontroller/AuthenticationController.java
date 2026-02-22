package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.LoginRequestDTO;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.JwtService;
import com.epam.finaltask.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
public class AuthenticationController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO,
                               BindingResult result,
                               Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.register(userDTO);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequestDTO());
        }
        return "auth/sign-in";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDTO loginRequest,
                        HttpServletResponse response,
                        Model model) {
        try {
            UserDTO user = userService.getUserByUsername(loginRequest.getUsername());
            if (!user.isActive()) {
                model.addAttribute("error", "You are blocked from using the service," +
                        " please contact the administrator (+380999999999)");
                model.addAttribute("loginRequest", loginRequest);
                return "auth/sign-in";
            }
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                model.addAttribute("error", "Invalid username or password");
                model.addAttribute("loginRequest", loginRequest);
                return "auth/sign-in";
            }

            String token = jwtService.generateToken(user.getUsername(), user.getRole());
            Cookie cookie = new Cookie("jwt", token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 3600);
            response.addCookie(cookie);
            return "redirect:/vouchers";

        } catch (Exception e) {
            model.addAttribute("error", "Invalid username or password");
            model.addAttribute("loginRequest", new LoginRequestDTO());
            return "auth/sign-in";
        }
    }


    @RequestMapping(value = "/logout", method = {RequestMethod.GET, RequestMethod.POST})
    public void logout(HttpServletResponse response) throws IOException {
        ResponseCookie jwtCookie = ResponseCookie.from("jwt", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        SecurityContextHolder.clearContext();

        response.sendRedirect("/login");
    }
}
