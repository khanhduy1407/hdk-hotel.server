package com.hdkhotel.controller;

import com.hdkhotel.exception.RoleAlreadyExistException;
import com.hdkhotel.model.Role;
import com.hdkhotel.model.User;
import com.hdkhotel.service.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FOUND;

@CrossOrigin
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {
  private final IRoleService roleService;

  @GetMapping("/all-roles")
  public ResponseEntity<List<Role>> getAllRoles() {
    return new ResponseEntity<>(roleService.getRoles(), FOUND);
  }

  @PostMapping("/create-new-role")
  public ResponseEntity<String> createRole(@RequestBody Role theRole) {
    try {
      roleService.createRole(theRole);
      return ResponseEntity.ok("New role created successfully!");
    } catch (RoleAlreadyExistException re) {
      return ResponseEntity.status(CONFLICT).body(re.getMessage());
    }
  }

  @DeleteMapping("/delete/{roleId}")
  public void deleteRole(@PathVariable("roleId") Long roleId) {
    roleService.deleteRole(roleId);
  }

  @PostMapping("/remove-all-users-from-role/{roleId}")
  public Role removeAllUsersFromRole(@PathVariable("roleId") Long roleId) {
    return roleService.removeAllUsersFromRole(roleId);
  }

  @PostMapping("/remove-user-from-role")
  public User removeUserFromRole(@RequestParam("userId") Long userId,
                                 @RequestParam("roleId") Long roleId) {
    return roleService.removeUserFromRole(userId, roleId);
  }

  @PostMapping("/assign-user-to-role")
  public User assignUserToRole(@RequestParam("userId") Long userId,
                                 @RequestParam("roleId") Long roleId) {
    return roleService.assignRoleToUser(userId, roleId);
  }
}
