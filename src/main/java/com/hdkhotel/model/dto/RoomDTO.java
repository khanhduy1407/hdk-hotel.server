package com.hdkhotel.model.dto;

import com.hdkhotel.model.enums.RoomType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
  private Long id;

  private Long hotelId;

  private RoomType roomType;

  @NotNull(message = "Sử dụng hình ảnh phòng mẫu tại https://sketchfab.com/")
  private String roomPreview;

  @NotNull(message = "Số phòng không được để trống")
  @PositiveOrZero(message = "Số phòng phải từ 0 trở lên")
  private Integer roomCount;

  @NotNull(message = "Giá không được để trống")
  @PositiveOrZero(message = "Giá mỗi đêm phải từ 0 trở lên")
  private Double pricePerNight;
}
