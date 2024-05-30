package com.hdkhotel.service.impl;

import com.hdkhotel.model.Hotel;
import com.hdkhotel.model.Room;
import com.hdkhotel.model.dto.RoomDTO;
import com.hdkhotel.repository.RoomRepository;
import com.hdkhotel.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomServiceImpl implements RoomService {
  private final RoomRepository roomRepository;

  @Override
  public Room saveRoom(RoomDTO roomDTO, Hotel hotel) {
    log.info("Đang cố gắng lưu một phòng mới: {}", roomDTO);
    Room room = mapRoomDtoToRoom(roomDTO, hotel);
    room = roomRepository.save(room);
    log.info("Đã lưu thành công phòng với ID: {}", room.getId());
    return room;
  }

  @Override
  public List<Room> saveRooms(List<RoomDTO> roomDTOs, Hotel hotel) {
    log.info("Đang cố gắng lưu các phòng: {}", roomDTOs);
    List<Room> rooms = roomDTOs.stream()
      .map(roomDTO -> saveRoom(roomDTO, hotel)) // save each room
      .collect(Collectors.toList());
    log.info("Đã lưu các phòng thành công: {}", rooms);
    return rooms;
  }

  @Override
  public Optional<Room> findRoomById(Long id) {
    return roomRepository.findById(id);
  }

  @Override
  public List<Room> findRoomsByHotelId(Long hotelId) {
    return null;
  }

  @Override
  public Room updateRoom(RoomDTO roomDTO) {
    log.info("Đang cố gắng cập nhật phòng với ID: {}", roomDTO.getId());
    Room existingRoom = roomRepository.findById(roomDTO.getId())
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phòng"));

    log.info("Phòng với ID: {} đã tìm thấy", roomDTO.getId());

    existingRoom.setRoomType(roomDTO.getRoomType());
    existingRoom.setRoomPreview(roomDTO.getRoomPreview());
    existingRoom.setRoomCount(roomDTO.getRoomCount());
    existingRoom.setPricePerNight(roomDTO.getPricePerNight());

    Room updatedRoom = roomRepository.save(existingRoom);
    log.info("Đã cập nhật thành công địa chỉ với ID: {}", existingRoom.getId());
    return updatedRoom;
  }

  @Override
  public void deleteRoom(Long id) {
    //
  }

  @Override
  public Room mapRoomDtoToRoom(RoomDTO roomDTO, Hotel hotel) {
    log.debug("Đang ánh xạ RoomDTO vào Room: {}", roomDTO);
    Room room = Room.builder()
      .hotel(hotel)
      .roomType(roomDTO.getRoomType())
      .roomPreview(roomDTO.getRoomPreview())
      .roomCount(roomDTO.getRoomCount())
      .pricePerNight(roomDTO.getPricePerNight())
      .build();
    log.debug("Đã ánh xạ Room: {}", room);
    return room;
  }

  @Override
  public RoomDTO mapRoomToRoomDto(Room room) {
    return RoomDTO.builder()
      .id(room.getId())
      .hotelId(room.getHotel().getId())
      .roomType(room.getRoomType())
      .roomPreview(room.getRoomPreview())
      .roomCount(room.getRoomCount())
      .pricePerNight(room.getPricePerNight())
      .build();
  }
}
