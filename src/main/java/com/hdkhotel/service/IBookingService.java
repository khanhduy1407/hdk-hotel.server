package com.hdkhotel.service;

import com.hdkhotel.model.BookedRoom;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IBookingService {
  List<BookedRoom> getAllBookingsByRoomId(Long roomId);

  void cancelBooking(Long bookingId);

  String saveBooking(Long roomId, BookedRoom bookingRequest);

  BookedRoom findByBookingConfirmationCode(String confirmationCode);

  List<BookedRoom> getAllBookings();

  List<BookedRoom> getBookingsByUserEmail(String email);
}
