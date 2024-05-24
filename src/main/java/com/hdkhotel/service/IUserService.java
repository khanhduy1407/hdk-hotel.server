package com.hdkhotel.service;

import com.hdkhotel.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IUserService {
  User registerUser(User user);
  List<User> getUsers();
  void deleteUser(String email);
  User getUser(String email);
}
