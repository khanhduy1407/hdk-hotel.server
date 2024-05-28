package com.hdkhotel.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
  private Long id;

  @NotBlank(message = "Địa chỉ không được để trống")
  @Pattern(regexp = "^[A-Za-z0-9 .,:-]*$", message = "Địa chỉ chỉ được chứa chữ cái, số và một số ký tự đặc biệt (. , : - )")
  private String addressLine;

  @NotBlank(message = "Thành phố không thể để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Thành phố chỉ được chứa các chữ cái")
  private String city;

  @NotBlank(message = "Quốc gia không thể để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z ]+$", message = "Quốc gia chỉ được chứa các chữ cái")
  private String country;
}
