package com.hdkhotel.model.dto;

import com.hdkhotel.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
  private Long id;

  @NotBlank(message = "Địa chỉ email không được để trống")
  @Email(message = "Địa chỉ email không hợp lệ")
  private String username;

  @NotBlank(message = "Tên không được để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Tên chỉ được chứa các chữ cái")
  private String name;

  @NotBlank(message = "Họ không được để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Họ chỉ được chứa các chữ cái")
  private String lastName;

  private Role role;
}
