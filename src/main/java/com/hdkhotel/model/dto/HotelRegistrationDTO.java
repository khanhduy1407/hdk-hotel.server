package com.hdkhotel.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelRegistrationDTO {
  @NotBlank(message = "Tên khách sạn không được để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z0-9 ]+$", message = "Tên khách sạn chỉ được chứa chữ cái và số")
  private String name;

  @Valid
  private AddressDTO addressDTO;

  @Valid
  private List<RoomDTO> roomDTOs = new ArrayList<>();
}
