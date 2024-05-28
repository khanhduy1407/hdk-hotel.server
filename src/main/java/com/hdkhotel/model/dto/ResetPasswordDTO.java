package com.hdkhotel.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResetPasswordDTO {
  @NotBlank(message = "Mật khẩu không được để trống")
  @Size(min = 6, max = 20, message = "Mật khẩu phải có từ 6 đến 20 ký tự")
  private String oldPassword;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Size(min = 6, max = 20, message = "Mật khẩu phải có từ 6 đến 20 ký tự")
  private String newPassword;

  @NotBlank(message = "Mật khẩu không được để trống")
  @Size(min = 6, max = 20, message = "Mật khẩu phải có từ 6 đến 20 ký tự")
  private String confirmNewPassword;
}
