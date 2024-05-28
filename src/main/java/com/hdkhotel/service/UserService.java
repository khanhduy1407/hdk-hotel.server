package com.hdkhotel.service;

import com.hdkhotel.model.User;
import com.hdkhotel.model.dto.ResetPasswordDTO;
import com.hdkhotel.model.dto.UserDTO;
import com.hdkhotel.model.dto.UserRegistrationDTO;

import java.util.List;

public interface UserService {
  User saveUser(UserRegistrationDTO registrationDTO);

  // For registration
  User findUserByUsername(String username);

  UserDTO findUserDTOByUsername(String username);

  UserDTO findUserById(Long id);

  List<UserDTO> findAllUsers();

  void updateUser(UserDTO userDTO);

  void updateLoggedInUser(UserDTO userDTO);

  void deleteUserById(Long id);

  User resetPassword(ResetPasswordDTO resetPasswordDTO);
}
