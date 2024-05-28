package com.hdkhotel.repository;

import com.hdkhotel.model.Role;
import com.hdkhotel.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  Role findByRoleType(RoleType roleType);
}
