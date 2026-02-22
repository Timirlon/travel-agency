package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.*;
import com.epam.finaltask.service.JwtService;
import com.epam.finaltask.service.UserService;
import com.epam.finaltask.service.VoucherService;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class VoucherControllerTest {

    @Mock
    private VoucherService voucherService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private VoucherController voucherController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllVouchers_WithValidToken_ShouldReturnView() {
        String token = "valid-token";
        UserDTO user = new UserDTO();
        user.setUsername("john");
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("john");
        when(jwtService.extractRole(token)).thenReturn("ADMIN");
        when(userService.getUserByUsername("john")).thenReturn(user);
        when(voucherService.findAllFilteredSorted(null, null, null, null))
                .thenReturn(List.of(new VoucherDTO()));

        String view = voucherController.getAllVouchers(token, null, null, null, null, model, response);

        assertEquals("vouchers/vouchers", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("isAdmin", true);
        verify(model).addAttribute("tourTypes", TourType.values());
        verify(model).addAttribute("vouchers", List.of(new VoucherDTO()));
    }

    @Test
    void getAllVouchers_InvalidToken_ShouldRedirect() throws Exception {
        String token = "invalid";
        when(jwtService.validateToken(token)).thenReturn(false);
        doNothing().when(response).sendRedirect("/login");

        String view = voucherController.getAllVouchers(token, null, null, null, null, model, response);

        assertNull(view);
        verify(response).sendRedirect("/login");
    }

    @Test
    void order_ShouldAddFlashSuccess() {
        // исправлено: мокируем через thenReturn
        when(voucherService.order(anyString(), anyString())).thenReturn(null);
        when(redirectAttributes.addFlashAttribute(anyString(), anyString())).thenReturn(redirectAttributes);

        String view = voucherController.order("voucher1", "user1", redirectAttributes);

        assertEquals("redirect:/vouchers", view);
        verify(redirectAttributes).addFlashAttribute("success", "Voucher successfully booked.");
    }

    @Test
    void toggleHot_ShouldCallService() {
        // исправлено: мокируем через thenReturn
        when(voucherService.changeHotStatus(anyString(), any(VoucherDTO.class))).thenReturn(null);

        String view = voucherController.toggleHot("v1", true, redirectAttributes);

        assertEquals("redirect:/vouchers", view);
        verify(voucherService).changeHotStatus(eq("v1"), any(VoucherDTO.class));
    }

    @Test
    void changeStatus_ShouldCallService() {
        // исправлено: мокируем через thenReturn
        when(voucherService.update(anyString(), any(VoucherDTO.class))).thenReturn(null);

        String view = voucherController.changeStatus("v1", "ACTIVE", redirectAttributes);

        assertEquals("redirect:/vouchers", view);
        verify(voucherService).update(eq("v1"), any(VoucherDTO.class));
    }

    @Test
    void addVoucher_ShouldCallService() {
        VoucherDTO dto = new VoucherDTO();
        // исправлено: мокируем через thenReturn
        when(voucherService.create(dto)).thenReturn(null);
        when(redirectAttributes.addFlashAttribute(anyString(), anyString())).thenReturn(redirectAttributes);

        String view = voucherController.addVoucher(dto, redirectAttributes);

        assertEquals("redirect:/vouchers", view);
        verify(voucherService).create(dto);
        verify(redirectAttributes).addFlashAttribute("success", "Voucher added.");
    }

    @Test
    void getUserVouchers_ShouldReturnMyVouchersView() {
        String token = "token";
        UserDTO user = new UserDTO();
        user.setId("user1");
        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractUsername(token)).thenReturn("john");
        when(userService.getUserByUsername("john")).thenReturn(user);
        when(voucherService.findAllByUserId(user.getId())).thenReturn(List.of(new VoucherDTO()));

        String view = voucherController.getUserVouchers(token, model, response);

        assertEquals("vouchers/my-vouchers", view);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("vouchers", List.of(new VoucherDTO()));
    }
}