package com.hdkhotel.controller;

import com.hdkhotel.exception.UserAlreadyExistsException;
import com.hdkhotel.model.User;
import com.hdkhotel.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
public class AuthController {
  private final IUserService userService;

  @PostMapping("/register-user")
  public ResponseEntity<?> registerUser(User user) {
    try {
      userService.registerUser(user);
      return ResponseEntity.ok("Registration successful");
    } catch (UserAlreadyExistsException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }
}
