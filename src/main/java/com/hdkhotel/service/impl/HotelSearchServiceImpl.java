package com.hdkhotel.service.impl;

import com.hdkhotel.model.Hotel;
import com.hdkhotel.model.enums.RoomType;
import com.hdkhotel.model.dto.AddressDTO;
import com.hdkhotel.model.dto.HotelAvailabilityDTO;
import com.hdkhotel.model.dto.RoomDTO;
import com.hdkhotel.repository.HotelRepository;
import com.hdkhotel.service.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelSearchServiceImpl implements HotelSearchService {
  private final HotelRepository hotelRepository;
  private final AddressService addressService;
  private final RoomService roomService;
  private final AvailabilityService availabilityService;

  @Override
  public List<HotelAvailabilityDTO> findAvailableHotelsByCityAndDate(String city, LocalDate checkinDate, LocalDate checkoutDate) {
    validateCheckinAndCheckoutDates(checkinDate, checkoutDate);

    log.info("Đang cố gắng tìm các khách sạn ở {} có phòng trống từ {} đến {}", city, checkinDate, checkoutDate);

    // Number of days between check-in and check-out
    Long numberOfDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);

    // 1. Fetch hotels that satisfy the criteria (min 1 available room throughout the booking range)
    List<Hotel> hotelsWithAvailableRooms = hotelRepository.findHotelsWithAvailableRooms(city, checkinDate, checkoutDate, numberOfDays);

    // 2. Fetch hotels that don't have any availability records for the entire booking range
    List<Hotel> hotelsWithoutAvailabilityRecords = hotelRepository.findHotelsWithoutAvailabilityRecords(city, checkinDate, checkoutDate);

    // 3. Fetch hotels with partial availability; some days with records meeting the criteria and some days without any records
    List<Hotel> hotelsWithPartialAvailabilityRecords = hotelRepository.findHotelsWithPartialAvailabilityRecords(city, checkinDate, checkoutDate, numberOfDays);

    // Combine and deduplicate the hotels using a Set
    Set<Hotel> combinedHotels = new HashSet<>(hotelsWithAvailableRooms);
    combinedHotels.addAll(hotelsWithoutAvailabilityRecords);
    combinedHotels.addAll(hotelsWithPartialAvailabilityRecords);

    log.info("Đã tìm thấy thành công {} khách sạn còn phòng trống", combinedHotels.size());

    // Convert the combined hotel list to DTOs for the response
    return combinedHotels.stream()
      .map(hotel -> mapHotelToHotelAvailabilityDto(hotel, checkinDate, checkoutDate))
      .collect(Collectors.toList());
  }

  @Override
  public HotelAvailabilityDTO findAvailableHotelById(Long hotelId, LocalDate checkinDate, LocalDate checkoutDate) {
    validateCheckinAndCheckoutDates(checkinDate, checkoutDate);

    log.info("Đang cố gắng tìm khách sạn có ID {} với các phòng trống từ {} đến {}", hotelId, checkinDate, checkoutDate);

    Optional<Hotel> hotelOptional = hotelRepository.findById(hotelId);
    if (hotelOptional.isEmpty()) {
      log.error("Không tìm thấy khách sạn nào có ID: {}", hotelId);
      throw new EntityNotFoundException("Không tìm thấy khách sạn");
    }

    Hotel hotel = hotelOptional.get();
    return mapHotelToHotelAvailabilityDto(hotel, checkinDate, checkoutDate);
  }


  @Override
  public HotelAvailabilityDTO mapHotelToHotelAvailabilityDto(Hotel hotel, LocalDate checkinDate, LocalDate checkoutDate) {
    List<RoomDTO> roomDTOs = hotel.getRooms().stream()
      .map(roomService::mapRoomToRoomDto)  // convert each Room to RoomDTO
      .collect(Collectors.toList());

    AddressDTO addressDTO = addressService.mapAddressToAddressDto(hotel.getAddress());

    HotelAvailabilityDTO hotelAvailabilityDTO = HotelAvailabilityDTO.builder()
      .id(hotel.getId())
      .name(hotel.getName())
      .addressDTO(addressDTO)
      .roomDTOs(roomDTOs)
      .build();

    // For each room type, find the minimum available rooms across the date range
    int maxAvailableSingleRooms = hotel.getRooms().stream()
      .filter(room -> room.getRoomType() == RoomType.SINGLE)
      .mapToInt(room -> availabilityService.getMinAvailableRooms(room.getId(), checkinDate, checkoutDate))
      .max()
      .orElse(0); // Assume no single rooms if none match the filter
    hotelAvailabilityDTO.setMaxAvailableSingleRooms(maxAvailableSingleRooms);

    int maxAvailableDoubleRooms = hotel.getRooms().stream()
      .filter(room -> room.getRoomType() == RoomType.DOUBLE)
      .mapToInt(room -> availabilityService.getMinAvailableRooms(room.getId(), checkinDate, checkoutDate))
      .max()
      .orElse(0); // Assume no double rooms if none match the filter
    hotelAvailabilityDTO.setMaxAvailableDoubleRooms(maxAvailableDoubleRooms);

    return hotelAvailabilityDTO;
  }

  private void validateCheckinAndCheckoutDates(LocalDate checkinDate, LocalDate checkoutDate) {
    if (checkinDate.isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("Ngày nhận phòng không được là ngày trong quá khứ");
    }
    if (checkoutDate.isBefore(checkinDate.plusDays(1))) {
      throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng");
    }
  }
}
