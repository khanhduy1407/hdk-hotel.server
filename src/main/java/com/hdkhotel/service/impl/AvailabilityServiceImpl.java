package com.hdkhotel.service.impl;

import com.hdkhotel.model.Availability;
import com.hdkhotel.model.Hotel;
import com.hdkhotel.model.Room;
import com.hdkhotel.model.dto.RoomSelectionDTO;
import com.hdkhotel.model.enums.RoomType;
import com.hdkhotel.repository.AvailabilityRepository;
import com.hdkhotel.service.AvailabilityService;
import com.hdkhotel.service.HotelService;
import com.hdkhotel.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {
  private final AvailabilityRepository availabilityRepository;
  private final HotelService hotelService;
  private final RoomService roomService;

  @Override
  public Integer getMinAvailableRooms(Long roomId, LocalDate checkinDate, LocalDate checkoutDate) {
    log.info("Đang tìm nạp số phòng trống tối thiểu cho ID phòng {} từ {} đến {}", roomId, checkinDate, checkoutDate);

    Room room = roomService.findRoomById(roomId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phòng"));

    // Fetch the minimum available rooms throughout the booking range for a room ID.
    return availabilityRepository.getMinAvailableRooms(roomId, checkinDate, checkoutDate)
      .orElse(room.getRoomCount()); // Consider no record as full availability
  }

  @Override
  public void updateAvailabilities(long hotelId, LocalDate checkinDate, LocalDate checkoutDate, List<RoomSelectionDTO> roomSelections) {
    Hotel hotel = hotelService.findHotelById(hotelId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách sạn"));
    log.info("Đang cố gắng cập nhật tình trạng phòng trống cho ID khách sạn {} từ {} đến {}", hotelId, checkinDate, checkoutDate);

    roomSelections = roomSelections.stream()
      .filter(roomSelection -> roomSelection.getCount() > 0)
      .collect(Collectors.toList());

    // Iterate through the room selections made by the user
    for (RoomSelectionDTO roomSelection : roomSelections) {
      RoomType roomType = roomSelection.getRoomType();
      int selectedCount = roomSelection.getCount();
      log.debug("Đang xử lý {} phòng thuộc loại {}", selectedCount, roomType);

      // Find the room by roomType for the given hotel
      Room room = hotel.getRooms().stream()
        .filter(r -> r.getRoomType() == roomType)
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy loại phòng"));

      // Iterate through the dates and update or create availability
      for (LocalDate date = checkinDate; date.isBefore(checkoutDate); date = date.plusDays(1)) {
        final LocalDate currentDate = date; // Temporary final variable
        Availability availability = availabilityRepository.findByRoomIdAndDate(room.getId(), date)
          .orElseGet(() -> Availability.builder()
            .hotel(hotel)
            .date(currentDate)
            .room(room)
            .availableRooms(room.getRoomCount())
            .build());

        // Reduce the available rooms by the selected count
        int updatedAvailableRooms = availability.getAvailableRooms() - selectedCount;
        if (updatedAvailableRooms < 0) {
          throw new IllegalArgumentException("Số phòng đã chọn vượt quá số phòng sẵn có trong ngày: " + currentDate);
        }
        availability.setAvailableRooms(updatedAvailableRooms);

        availabilityRepository.save(availability);
      }
    }
    log.info("Đã cập nhật thành công tình trạng phòng trống cho ID khách sạn {} từ {} đến {}", hotelId, checkinDate, checkoutDate);
  }
}
