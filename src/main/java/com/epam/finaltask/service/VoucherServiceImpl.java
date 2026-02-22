package com.epam.finaltask.service;

import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.*;
import com.epam.finaltask.repository.UserRepository;
import com.epam.finaltask.repository.VoucherRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public VoucherServiceImpl(VoucherRepository voucherRepository,
                              UserRepository userRepository,
                              ModelMapper modelMapper) {
        this.voucherRepository = voucherRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public VoucherDTO create(VoucherDTO voucherDTO) {
        Voucher voucher = modelMapper.map(voucherDTO, Voucher.class);
        voucher.setId(UUID.randomUUID());

        voucher.setTourType(TourType.valueOf(voucherDTO.getTourType()));
        voucher.setTransferType(TransferType.valueOf(voucherDTO.getTransferType()));
        voucher.setHotelType(HotelType.valueOf(voucherDTO.getHotelType()));
        voucher.setStatus(VoucherStatus.valueOf(voucherDTO.getStatus()));

        if (voucherDTO.getUserId() != null) {
            User user = userRepository.findById(voucherDTO.getUserId())
                    .orElseThrow(() -> new NoSuchElementException("User not found"));
            voucher.setUser(user);
        }

        voucher.setHot(Boolean.TRUE.equals(voucherDTO.getIsHot()));
        return convertToDTO(voucherRepository.save(voucher));
    }

    @Override
    public VoucherDTO order(String voucherId, String userId) {
        UUID vId = UUID.fromString(voucherId);
        UUID uId = UUID.fromString(userId);

        Voucher voucher = voucherRepository.findById(vId)
                .orElseThrow(() -> new NoSuchElementException("Voucher not found"));
        User user = userRepository.findById(uId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        if (user.getBalance().doubleValue() < voucher.getPrice()) {
            throw new IllegalStateException("Not enough balance.");
        }

        user.setBalance(user.getBalance().subtract(BigDecimal.valueOf(voucher.getPrice())));
        voucher.setUser(user);
        voucher.setStatus(VoucherStatus.PAID);

        userRepository.save(user);
        return convertToDTO(voucherRepository.save(voucher));
    }

    @Override
    public VoucherDTO update(String id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("Voucher not found"));

        voucher.setTitle(voucherDTO.getTitle());
        voucher.setDescription(voucherDTO.getDescription());
        voucher.setPrice(voucherDTO.getPrice());
        voucher.setArrivalDate(voucherDTO.getArrivalDate());
        voucher.setEvictionDate(voucherDTO.getEvictionDate());

        if (voucherDTO.getTourType() != null)
            voucher.setTourType(TourType.valueOf(voucherDTO.getTourType()));
        if (voucherDTO.getTransferType() != null)
            voucher.setTransferType(TransferType.valueOf(voucherDTO.getTransferType()));
        if (voucherDTO.getHotelType() != null)
            voucher.setHotelType(HotelType.valueOf(voucherDTO.getHotelType()));
        if (voucherDTO.getStatus() != null)
            voucher.setStatus(VoucherStatus.valueOf(voucherDTO.getStatus()));
        if (voucherDTO.getIsHot() != null)
            voucher.setHot(voucherDTO.getIsHot());

        return convertToDTO(voucherRepository.save(voucher));
    }

    @Override
    public void delete(String voucherId) {
        UUID id = UUID.fromString(voucherId);
        if (!voucherRepository.existsById(id)) {
            throw new NoSuchElementException("Voucher not found");
        }
        voucherRepository.deleteById(id);
    }

    @Override
    public VoucherDTO changeHotStatus(String id, VoucherDTO voucherDTO) {
        Voucher voucher = voucherRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new NoSuchElementException("Voucher not found"));

        voucher.setHot(Boolean.TRUE.equals(voucherDTO.getIsHot()));
        return convertToDTO(voucherRepository.save(voucher));
    }

    @Override
    public List<VoucherDTO> findAllByUserId(String userId) {
        UUID id = UUID.fromString(userId);
        return voucherRepository.findAllByUserId(id).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTourType(TourType tourType) {
        return voucherRepository.findAllByTourType(tourType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByTransferType(String transferType) {
        return voucherRepository.findAllByTransferType(TransferType.valueOf(transferType)).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByPrice(Double price) {
        return voucherRepository.findAllByPrice(price).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAllByHotelType(HotelType hotelType) {
        return voucherRepository.findAllByHotelType(hotelType).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoucherDTO> findAll() {
        return voucherRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private VoucherDTO convertToDTO(Voucher voucher) {
        VoucherDTO dto = modelMapper.map(voucher, VoucherDTO.class);

        if (voucher.getUser() != null) {
            dto.setUserId(voucher.getUser().getId());
        }

        dto.setTourType(voucher.getTourType() != null ? voucher.getTourType().name() : null);
        dto.setTransferType(voucher.getTransferType() != null ? voucher.getTransferType().name() : null);
        dto.setHotelType(voucher.getHotelType() != null ? voucher.getHotelType().name() : null);
        dto.setStatus(voucher.getStatus() != null ? voucher.getStatus().name() : null);
        dto.setIsHot(voucher.isHot());

        return dto;
    }

    @Override
    public List<VoucherDTO> findAllFilteredSorted(Double maxPrice, String tourType, String transferType, String hotelType) {
        return voucherRepository.findAll().stream()
                .filter(v -> (maxPrice == null || v.getPrice() <= maxPrice))
                .filter(v -> (tourType == null || tourType.isEmpty() || v.getTourType().name().equals(tourType)))
                .filter(v -> (transferType == null || transferType.isEmpty() || v.getTransferType().name().equals(transferType)))
                .filter(v -> (hotelType == null || hotelType.isEmpty() || v.getHotelType().name().equals(hotelType)))
                .filter(v -> v.getUser() == null || v.getStatus() != VoucherStatus.PAID)
                .sorted((v1, v2) -> Boolean.compare(v2.isHot(), v1.isHot()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}