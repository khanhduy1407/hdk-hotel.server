package com.hdkhotel.service;

import com.hdkhotel.model.BookedRoom;

import java.util.List;

public interface IBookingService {
  List<BookedRoom> getAllBookingsByRoomId(Long roomId);

  void cancelBooking(Long bookingId);

  String saveBooking(Long roomId, BookedRoom bookingRequest);

  BookedRoom findByBookingConfirmationCode(String confirmationCode);

  List<BookedRoom> getAllBookings();
}
