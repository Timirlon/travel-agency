package com.epam.finaltask.restcontroller;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.service.VoucherService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherRestController {

    private final VoucherService voucherService;

    public VoucherRestController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody VoucherDTO voucherDTO) {
        VoucherDTO created = voucherService.create(voucherDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "statusCode", "OK",
                        "statusMessage", "Voucher is successfully created",
                        "results", created
                ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO updated = voucherService.update(id, voucherDTO);
        return ResponseEntity.ok(Map.of(
                "statusCode", "OK",
                "statusMessage", "Voucher is successfully updated",
                "results", updated
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        voucherService.delete(id);
        return ResponseEntity.ok(Map.of(
                "statusCode", "OK",
                "statusMessage", String.format("Voucher with Id %s has been deleted", id)
        ));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> changeHotStatus(@PathVariable String id, @RequestBody VoucherDTO voucherDTO) {
        VoucherDTO changed = voucherService.changeHotStatus(id, voucherDTO);
        return ResponseEntity.ok(Map.of(
                "statusCode", "OK",
                "statusMessage", "Voucher status is successfully changed",
                "results", changed
        ));
    }

    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String tourType,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String transferType,
            @RequestParam(required = false) String hotelType,
            @RequestParam(defaultValue = "false") boolean onlyHot) {

        List<VoucherDTO> all = voucherService.findAll();

        if (tourType != null) {
            all = all.stream()
                    .filter(v -> tourType.equalsIgnoreCase(v.getTourType()))
                    .collect(Collectors.toList());
        }

        if (price != null) {
            all = all.stream()
                    .filter(v -> v.getPrice() <= price)
                    .collect(Collectors.toList());
        }

        if (transferType != null) {
            all = all.stream()
                    .filter(v -> transferType.equalsIgnoreCase(v.getTransferType()))
                    .collect(Collectors.toList());
        }

        if (hotelType != null) {
            all = all.stream()
                    .filter(v -> hotelType.equalsIgnoreCase(v.getHotelType()))
                    .collect(Collectors.toList());
        }

        if (onlyHot) {
            all = all.stream()
                    .filter(VoucherDTO::getIsHot)
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(Map.of(
                "statusCode", "OK",
                "statusMessage", "List of vouchers",
                "results", all
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getByUser(@PathVariable String userId) {
        List<VoucherDTO> userVouchers = voucherService.findAllByUserId(userId);
        return ResponseEntity.ok(Map.of(
                "statusCode", "OK",
                "statusMessage", "List of user's vouchers",
                "results", userVouchers
        ));
    }
}
