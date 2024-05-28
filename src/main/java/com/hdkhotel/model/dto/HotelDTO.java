package com.hdkhotel.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class HotelDTO {
  private Long id;

  @NotBlank(message = "Tên khách sạn không được để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z0-9 ]+$", message = "Tên khách sạn chỉ được chứa chữ cái và số")
  private String name;

  @Valid
  private AddressDTO addressDTO;

  @Valid
  private List<RoomDTO> roomDTOs = new ArrayList<>();

  private String managerUsername;
}
