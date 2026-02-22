package com.epam.finaltask.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import com.epam.finaltask.dto.UserDTO;
import com.epam.finaltask.mapper.UserMapper;
import com.epam.finaltask.mapper.VoucherMapper;
import com.epam.finaltask.model.Role;
import com.epam.finaltask.model.User;
import com.epam.finaltask.model.Voucher;
import com.epam.finaltask.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final VoucherMapper voucherMapper;
	private final PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, VoucherMapper voucherMapper, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.voucherMapper = voucherMapper;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDTO register(UserDTO userDTO) {
		if (userRepository.existsByUsername(userDTO.getUsername())) {
			throw new IllegalArgumentException("Username already exists.");
		}

		User user = userMapper.toUser(userDTO);
		user.setId(UUID.randomUUID());
		user.setActive(true);
		user.setRole(Role.USER);
		user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

		if (user.getBalance() == null) {
			user.setBalance(BigDecimal.ZERO);
		}

		User savedUser = userRepository.save(user);
		return userMapper.toUserDTO(savedUser);
	}

	@Override
	public UserDTO updateUser(String username, UserDTO userDTO) {
		User existingUser = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new NoSuchElementException("User not found"));

		existingUser.setPhoneNumber(userDTO.getPhoneNumber());

		if (userDTO.getBalance() != null) {
			existingUser.setBalance(BigDecimal.valueOf(userDTO.getBalance()));
		}
		if (userDTO.getRole() != null) {
			existingUser.setRole(Role.valueOf(userDTO.getRole()));
		}
		if (userDTO.getVouchers() != null) {
			List<Voucher> vouchers = userDTO.getVouchers().stream()
					.map(voucherMapper::toVoucher)
					.collect(Collectors.toList());
			vouchers.forEach(v -> v.setUser(existingUser));

			existingUser.setVouchers(vouchers);
		}

		User updatedUser = userRepository.save(existingUser);
		return userMapper.toUserDTO(updatedUser);
	}

	@Override
	public UserDTO getUserByUsername(String username) {
		User user = userRepository.findUserByUsername(username)
				.orElseThrow(() -> new NoSuchElementException("User not found"));
		return userMapper.toUserDTO(user);
	}

	@Override
	public UserDTO changeAccountStatus(UserDTO userDTO) {
		UUID userId = UUID.fromString(userDTO.getId());
		User existingUser = userRepository.findById(userId)
				.orElseThrow(() -> new NoSuchElementException("User not found"));

		User updatedUser = userMapper.toUser(userDTO);
		updatedUser.setId(existingUser.getId());
		updatedUser.setUsername(existingUser.getUsername());
		updatedUser.setPassword(existingUser.getPassword());
		updatedUser.setPhoneNumber(existingUser.getPhoneNumber());
		updatedUser.setBalance(existingUser.getBalance());
		updatedUser.setRole(existingUser.getRole());
		updatedUser.setVouchers(existingUser.getVouchers());

		User saved = userRepository.save(updatedUser);
		return userMapper.toUserDTO(saved);
	}

	@Override
	public UserDTO getUserById(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("User not found"));
		return userMapper.toUserDTO(user);
	}

	@Override
	public List<UserDTO> findAll(){
		return userRepository.findAll().stream().map(userMapper::toUserDTO).toList();
	}

	@Override
	public Page<UserDTO> findAll(Pageable pageable) {
		return userRepository.findAll(pageable)
				.map(userMapper::toUserDTO);
	}

	@Override
	public Page<UserDTO> searchByUsername(String username, Pageable pageable) {
		return userRepository.findByUsernameContainingIgnoreCase(username, pageable)
				.map(userMapper::toUserDTO);
	}

	@Override
	public UserDTO changeUserRole(UUID userId, String newRole) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new NoSuchElementException("User not found"));

		user.setRole(Role.valueOf(newRole.toUpperCase()));
		User updated = userRepository.save(user);
		return userMapper.toUserDTO(updated);
	}
}
