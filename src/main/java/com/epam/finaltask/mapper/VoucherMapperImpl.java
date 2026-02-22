package com.epam.finaltask.mapper;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class VoucherMapperImpl implements VoucherMapper {
    @Override
    public Voucher toVoucher(VoucherDTO voucherDTO) {
        Voucher voucher = new Voucher();
        voucher.setId(UUID.fromString(voucherDTO.getId()));
        voucher.setTitle(voucherDTO.getTitle());
        voucher.setDescription(voucherDTO.getDescription());
        voucher.setPrice(voucherDTO.getPrice());
        voucher.setTourType(voucherDTO.getTourType() != null ? TourType.valueOf(voucherDTO.getTourType()) : null);
        voucher.setTransferType(voucherDTO.getTransferType() != null ? TransferType.valueOf(voucherDTO.getTransferType()) : null);
        voucher.setHotelType(voucherDTO.getHotelType() != null ? HotelType.valueOf(voucherDTO.getHotelType()) : null);
        voucher.setStatus(voucherDTO.getStatus() != null ? VoucherStatus.valueOf(voucherDTO.getStatus()) : null);
        voucher.setArrivalDate(voucherDTO.getArrivalDate());
        voucher.setEvictionDate(voucherDTO.getEvictionDate());
        voucher.setHot(Boolean.TRUE.equals(voucherDTO.getIsHot()));
        return voucher;
    }

    @Override
    public VoucherDTO toVoucherDTO(Voucher voucher) {
        VoucherDTO dto = new VoucherDTO();
        dto.setId(voucher.getId().toString());
        dto.setTitle(voucher.getTitle());
        dto.setDescription(voucher.getDescription());
        dto.setPrice(voucher.getPrice());
        dto.setTourType(voucher.getTourType() != null ? voucher.getTourType().name() : null);
        dto.setTransferType(voucher.getTransferType() != null ? voucher.getTransferType().name() : null);
        dto.setHotelType(voucher.getHotelType() != null ? voucher.getHotelType().name() : null);
        dto.setStatus(voucher.getStatus() != null ? voucher.getStatus().name() : null);
        dto.setArrivalDate(voucher.getArrivalDate());
        dto.setEvictionDate(voucher.getEvictionDate());
        dto.setUserId(voucher.getUser() != null ? voucher.getUser().getId() : null);
        dto.setIsHot(voucher.isHot());
        return dto;
    }
}
