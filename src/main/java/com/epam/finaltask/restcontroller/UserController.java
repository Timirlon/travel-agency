package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllUsers(@RequestParam(value = "username", required = false) String username,
                              @RequestParam(value = "page", defaultValue = "0") int page,
                              @RequestParam(value = "size", defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());
        Page<UserDTO> userPage;

        if (username != null && !username.isBlank()) {
            userPage = userService.searchByUsername(username, pageable);
            model.addAttribute("searchedUsername", username);
        } else {
            userPage = userService.findAll(pageable);
        }

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("page", userPage);
        return "user/users";
    }

    @PostMapping("/role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserRole(@PathVariable("id") UUID id,
                                            @RequestParam("role") String newRole) {
        try {
            userService.changeUserRole(id, newRole);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update role.");
        }
    }

    @PostMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeUserStatus(@PathVariable("id") UUID id,
                                              @RequestParam("active") boolean active) {
        try {
            UserDTO user = userService.getUserById(id);
            user.setActive(active);
            userService.changeAccountStatus(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update status.");
        }
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable("id") UUID id,
                             @RequestParam("role") String role,
                             @RequestParam(value = "active", required = false) String active,
                             RedirectAttributes redirectAttributes) {
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(id.toString());
            userDTO.setActive("on".equals(active));
            userService.changeUserRole(id, role);
            userService.changeAccountStatus(userDTO);

            redirectAttributes.addFlashAttribute("success", "User updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update user.");
        }

        return "redirect:/users";
    }
}
