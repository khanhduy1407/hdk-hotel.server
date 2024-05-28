package com.hdkhotel.service;

import com.hdkhotel.model.Hotel;
import com.hdkhotel.model.Room;
import com.hdkhotel.model.dto.RoomDTO;

import java.util.List;
import java.util.Optional;

public interface RoomService {
  Room saveRoom(RoomDTO roomDTO, Hotel hotel);

  List<Room> saveRooms(List<RoomDTO> roomDTOs, Hotel hotel);

  Optional<Room> findRoomById(Long id);

  List<Room> findRoomsByHotelId(Long hotelId);

  Room updateRoom(RoomDTO roomDTO);

  void deleteRoom(Long id);

  Room mapRoomDtoToRoom(RoomDTO roomDTO, Hotel hotel);

  RoomDTO mapRoomToRoomDto(Room room);
}
