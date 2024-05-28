package com.hdkhotel.model.dto;

import com.hdkhotel.model.enums.RoleType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationDTO {
  @NotBlank(message = "Địa chỉ email không được để trống")
  @Email(message = "Địa chỉ email không hợp lệ")
  private String username;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Size(min = 6, max = 20, message = "Mật khẩu phải có từ 6 đến 20 ký tự")
  private String password;

  @NotBlank(message = "Tên không được để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Tên chỉ được chứa các chữ cái")
  private String name;

  @NotBlank(message = "Họ không được để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Họ chỉ được chứa các chữ cái")
  private String lastName;

  private RoleType roleType;
}
