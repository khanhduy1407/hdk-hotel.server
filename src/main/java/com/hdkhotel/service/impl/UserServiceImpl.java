package com.hdkhotel.service.impl;

import com.hdkhotel.exception.UsernameAlreadyExistsException;
import com.hdkhotel.model.*;
import com.hdkhotel.model.dto.ResetPasswordDTO;
import com.hdkhotel.model.dto.UserDTO;
import com.hdkhotel.model.dto.UserRegistrationDTO;
import com.hdkhotel.model.enums.RoleType;
import com.hdkhotel.repository.CustomerRepository;
import com.hdkhotel.repository.HotelManagerRepository;
import com.hdkhotel.repository.RoleRepository;
import com.hdkhotel.repository.UserRepository;
import com.hdkhotel.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final CustomerRepository customerRepository;
  private final HotelManagerRepository hotelManagerRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public User saveUser(UserRegistrationDTO registrationDTO) {
    log.info("Đang cố gắng lưu một người dùng mới: {}", registrationDTO.getUsername());

    Optional<User> existingUser = Optional.ofNullable(userRepository.findByUsername(registrationDTO.getUsername()));
    if (existingUser.isPresent()) {
      throw new UsernameAlreadyExistsException("Tên người dùng này đã được đăng ký!");
    }

    User user = mapRegistrationDtoToUser(registrationDTO);

    if (RoleType.CUSTOMER.equals(registrationDTO.getRoleType())) {
      Customer customer = Customer.builder().user(user).build();
      customerRepository.save(customer);
    } else if (RoleType.HOTEL_MANAGER.equals(registrationDTO.getRoleType())) {
      HotelManager hotelManager = HotelManager.builder().user(user).build();
      hotelManagerRepository.save(hotelManager);
    }

    User savedUser = userRepository.save(user);
    log.info("Đã lưu thành công người dùng mới: {}", registrationDTO.getUsername());
    return savedUser;
  }

  @Override
  public User findUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  @Override
  public UserDTO findUserDTOByUsername(String username) {
    Optional<User> userOptional = Optional.ofNullable(userRepository.findByUsername(username));
    User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tên người dùng"));

    return mapUserToUserDto(user);
  }

  @Override
  public UserDTO findUserById(Long id) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách sạn"));

    return mapUserToUserDto(user);
  }

  @Override
  public List<UserDTO> findAllUsers() {
    List<User> userList = userRepository.findAll();

    List<UserDTO> userDTOList = new ArrayList<>();
    for (User user : userList) {
      UserDTO userDTO = mapUserToUserDto(user);
      userDTOList.add(userDTO);
    }
    return userDTOList;
  }

  @Override
  @Transactional
  public void updateUser(UserDTO userDTO) {
    log.info("Đang cố gắng cập nhật người dùng với ID: {}", userDTO.getId());

    User user = userRepository.findById(userDTO.getId())
      .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy người dùng"));

    if (usernameExistsAndNotSameUser(userDTO.getUsername(), user.getId())) {
      throw new UsernameAlreadyExistsException("Tên người dùng này đã được đăng ký!");
    }

    setFormattedDataToUser(user, userDTO);
    userRepository.save(user);
    log.info("Đã cập nhật thành công người dùng hiện có với ID: {}", userDTO.getId());
  }

  @Override
  @Transactional
  public void updateLoggedInUser(UserDTO userDTO) {
    String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    User loggedInUser = userRepository.findByUsername(loggedInUsername);
    log.info("Đang cố gắng cập nhật người dùng đã đăng nhập với ID: {}", loggedInUser.getId());

    if (usernameExistsAndNotSameUser(userDTO.getUsername(), loggedInUser.getId())) {
      throw new UsernameAlreadyExistsException("Tên người dùng này đã được đăng ký!");
    }

    setFormattedDataToUser(loggedInUser, userDTO);
    userRepository.save(loggedInUser);
    log.info("Đã cập nhật thành công người dùng đã đăng nhập với ID: {}", loggedInUser.getId());

    // Create new authentication token
    updateAuthentication(userDTO);
  }

  @Override
  public void deleteUserById(Long id) {
    log.info("Đang cố gắng xóa người dùng với ID: {}", id);
    userRepository.deleteById(id);
    log.info("Đã xóa thành công người dùng với ID: {}", id);
  }

  @Override
  public User resetPassword(ResetPasswordDTO resetPasswordDTO) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String loggedInUsername = auth.getName();

    User user = userRepository.findByUsername(loggedInUsername);
    if (user == null) {
      throw new UsernameNotFoundException("Không tìm thấy người dùng");
    }
    if (!passwordEncoder.matches(resetPasswordDTO.getOldPassword(), user.getPassword())) {
      throw new IllegalArgumentException("Mật khẩu cũ không đúng");
    }
    user.setPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
    return userRepository.save(user);
  }

  private User mapRegistrationDtoToUser(UserRegistrationDTO registrationDTO) {
    Role userRole = roleRepository.findByRoleType(registrationDTO.getRoleType());
    return User.builder()
      .username(registrationDTO.getUsername().trim())
      .password(passwordEncoder.encode(registrationDTO.getPassword()))
      .name(formatText(registrationDTO.getName()))
      .lastName(formatText(registrationDTO.getLastName()))
      .role(userRole)
      .build();
  }

  private UserDTO mapUserToUserDto(User user) {
    return UserDTO.builder()
      .id(user.getId())
      .username(user.getUsername())
      .name(user.getName())
      .lastName(user.getLastName())
      .role(user.getRole())
      .build();
  }

  private boolean usernameExistsAndNotSameUser(String username, Long userId) {
    Optional<User> existingUserWithSameUsername = Optional.ofNullable(userRepository.findByUsername(username));
    return existingUserWithSameUsername.isPresent() && !existingUserWithSameUsername.get().getId().equals(userId);
  }

  private String formatText(String text) {
    return StringUtils.capitalize(text.trim());
  }

  private void setFormattedDataToUser(User user, UserDTO userDTO) {
    user.setUsername(userDTO.getUsername());
    user.setName(formatText(userDTO.getName()));
    user.setLastName(formatText(userDTO.getLastName()));
  }

  // In production applications, prefer logging out the user and requiring re-login over the method below.
  // It updates the authentication context directly, which could be a potential security risk.
  private void updateAuthentication(UserDTO userDTO) {
    User user = userRepository.findByUsername(userDTO.getUsername());

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleType().name()));

    UserDetails newUserDetails = new org.springframework.security.core.userdetails.User(
      user.getUsername(),
      user.getPassword(),
      authorities
    );

    UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(
      newUserDetails,
      null,
      newUserDetails.getAuthorities()
    );
    SecurityContextHolder.getContext().setAuthentication(newAuthentication);
  }
}
