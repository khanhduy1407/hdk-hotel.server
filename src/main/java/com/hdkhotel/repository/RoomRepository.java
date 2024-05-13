package com.hdkhotel.repository;

import com.hdkhotel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

  @Query("select distinct r.roomType from Room r")
  List<String> findDistinctRoomTypes();
}
