package com.hdkhotel.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_roles")
public class UserRole {

  @Id
  @Column(name = "user_id")
  private Long userId;

  @Id
  @Column(name = "role_id")
  private Long roleId;

  @ManyToOne
  @JoinColumn(name = "user_id", insertable = false, updatable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "role_id", insertable = false, updatable = false)
  private Role role;
}

