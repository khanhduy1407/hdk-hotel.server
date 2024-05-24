package com.hdkhotel.service;

import com.hdkhotel.model.Role;
import com.hdkhotel.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IRoleService {
  List<Role> getRoles();
  Role createRole(Role theRole);

  void deleteRole(Long id);
  Role findByName(String name);

  User removeUserFromRole(Long userId, Long roleId);
  User assignRoleToUser(Long userId, Long roleId);
  Role removeAllUsersFromRole(Long roleId);
}
