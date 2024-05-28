package com.hdkhotel.model.dto;

import com.hdkhotel.model.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomSelectionDTO {
  private RoomType roomType;
  private int count;
}
