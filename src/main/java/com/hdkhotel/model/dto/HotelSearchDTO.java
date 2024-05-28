package com.hdkhotel.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HotelSearchDTO {
  @NotBlank(message = "Thành phố không thể để trống")
  @Pattern(regexp = "^(?!\\s*$)[A-Za-z '-]+$", message = "Thành phố chỉ được chứa các chữ cái, dấu nháy đơn(') hoặc dấu gạch ngang(-)")
  private String city;

  @NotNull(message = "Ngày nhận phòng không được để trống")
  @FutureOrPresent(message = "Ngày nhận phòng không được là ngày trong quá khứ")
  private LocalDate checkinDate;

  @NotNull(message = "Ngày trả phòng không được để trống")
  private LocalDate checkoutDate;
}
