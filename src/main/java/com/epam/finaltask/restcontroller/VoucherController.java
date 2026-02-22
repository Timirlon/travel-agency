package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.HotelType;
import com.epam.finaltask.model.TourType;
import com.epam.finaltask.model.TransferType;
import com.epam.finaltask.model.VoucherStatus;
import com.epam.finaltask.service.JwtService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/vouchers")
public class VoucherController {

    private final VoucherService voucherService;
    private final UserService userService;
    private final JwtService jwtService;

    public VoucherController(VoucherService voucherService, UserService userService,
                             JwtService jwtService) {
        this.voucherService = voucherService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public String getAllVouchers(@CookieValue(name = "jwt", required = false) String token,
                                 @RequestParam(required = false) Double maxPrice,
                                 @RequestParam(required = false) String tourType,
                                 @RequestParam(required = false) String transferType,
                                 @RequestParam(required = false) String hotelType,
                                 Model model,
                                 HttpServletResponse response) {

        if (token == null || !jwtService.validateToken(token)) {
            try {
                response.sendRedirect("/login");
            } catch (IOException e) {
                throw new RuntimeException("Redirect failed", e);
            }
            return null;
        }

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);
        UserDTO user = userService.getUserByUsername(username);

        model.addAttribute("user", user);
        model.addAttribute("isAdmin", role.equals("ADMIN"));
        model.addAttribute("isManager", role.equals("MANAGER"));

        model.addAttribute("tourTypes", TourType.values());
        model.addAttribute("transferTypes", TransferType.values());
        model.addAttribute("hotelTypes", HotelType.values());
        model.addAttribute("statuses", VoucherStatus.values());

        List<VoucherDTO> vouchers = voucherService.findAllFilteredSorted(
                maxPrice, tourType, transferType, hotelType
        );
        model.addAttribute("vouchers", vouchers);

        return "vouchers/vouchers";
    }

    @PostMapping("/order/{id}")
    public String order(@PathVariable String id,
                        @RequestParam("userId") String userId,
                        RedirectAttributes redirectAttributes) {
        try {
            voucherService.order(id, userId);
            redirectAttributes.addFlashAttribute("success", "Voucher successfully booked.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking failed: " + e.getMessage());
        }
        return "redirect:/vouchers";
    }

    @PostMapping("/hot/{id}")
    public String toggleHot(@PathVariable String id,
                            @RequestParam boolean isHot,
                            RedirectAttributes redirectAttributes) {
        try {
            VoucherDTO dto = new VoucherDTO();
            dto.setIsHot(isHot);
            voucherService.changeHotStatus(id, dto);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change hot status.");
        }
        return "redirect:/vouchers";
    }

    @PostMapping("/status/{id}")
    public String changeStatus(@PathVariable String id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        try {
            VoucherDTO dto = new VoucherDTO();
            dto.setStatus(status);
            voucherService.update(id, dto);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change status.");
        }
        return "redirect:/vouchers";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public String addVoucher(@ModelAttribute VoucherDTO dto,
                             RedirectAttributes redirectAttributes) {
        try {
            voucherService.create(dto);
            redirectAttributes.addFlashAttribute("success", "Voucher added.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add voucher.");
        }
        return "redirect:/vouchers";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable String id,
                         RedirectAttributes redirectAttributes) {
        try {
            voucherService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Voucher deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete voucher.");
        }
        return "redirect:/vouchers";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/edit/{id}")
    public String editVoucher(@PathVariable String id,
                              @ModelAttribute VoucherDTO dto,
                              RedirectAttributes redirectAttributes) {
        try {
            voucherService.update(id, dto);
            redirectAttributes.addFlashAttribute("success", "Voucher updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update voucher: " + e.getMessage());
        }
        return "redirect:/vouchers";
    }

    @GetMapping("/myVouchers")
    public String getUserVouchers(@CookieValue(name = "jwt", required = false) String token,
                                  Model model,
                                  HttpServletResponse response) {
        if (token == null || !jwtService.validateToken(token)) {
            try {
                response.sendRedirect("/login");
            } catch (IOException e) {
                throw new RuntimeException("Redirect failed", e);
            }
            return null;
        }

        String username = jwtService.extractUsername(token);
        UserDTO user = userService.getUserByUsername(username);
        List<VoucherDTO> userVouchers = voucherService.findAllByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("vouchers", userVouchers);
        return "vouchers/my-vouchers";
    }
}
