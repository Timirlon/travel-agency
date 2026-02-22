package com.epam.finaltask.mapper;
import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.dto.VoucherDTO;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {

    private final ModelMapper modelMapper;
    private final VoucherMapper voucherMapper;

    public UserMapperImpl(ModelMapper modelMapper, VoucherMapper voucherMapper) {
        this.modelMapper = modelMapper;
        this.voucherMapper = voucherMapper;
    }

    @Override
    public User toUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        if (userDTO.getRole() != null) {
            user.setRole(Role.valueOf(userDTO.getRole()));
        }
        if (userDTO.getBalance() != null) {
            user.setBalance(BigDecimal.valueOf(userDTO.getBalance()));
        }
        return user;
    }

    @Override
    public UserDTO toUserDTO(User user) {
        UserDTO dto = modelMapper.map(user, UserDTO.class);

        if (user.getRole() != null) {
            dto.setRole(user.getRole().name());
        }

        if (user.getBalance() != null) {
            dto.setBalance(user.getBalance().doubleValue());
        }

        if (user.getVouchers() != null) {
            List<VoucherDTO> voucherDTOs = user.getVouchers().stream()
                    .map(voucherMapper::toVoucherDTO)
                    .collect(Collectors.toList());
            dto.setVouchers(voucherDTOs);
        }

        return dto;
    }
}
