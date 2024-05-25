package com.hdkhotel.service;

import com.hdkhotel.exception.InvalidBookingRequestException;
import com.hdkhotel.exception.ResourceNotFoundException;
import com.hdkhotel.model.BookedRoom;
import com.hdkhotel.model.Room;
import com.hdkhotel.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService implements IBookingService {
  private final BookingRepository bookingRepository;
  private final IRoomService roomService;

  @Override
  public List<BookedRoom> getAllBookings() {
    return bookingRepository.findAll();
  }

  @Override
  public List<BookedRoom> getBookingsByUserEmail(String email) {
    return bookingRepository.findByGuestEmail(email);
  }

  @Override
  public void cancelBooking(Long bookingId) {
    bookingRepository.deleteById(bookingId);
  }

  @Override
  public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
    return bookingRepository.findByRoomId(roomId);
  }

  @Override
  public String saveBooking(Long roomId, BookedRoom bookingRequest) {
    if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
      throw new InvalidBookingRequestException("Ngày nhận phòng phải trước ngày trả phòng.");
    }
    Room room = roomService.getRoomById(roomId).get();
    List<BookedRoom> existingBookings = room.getBookings();
    boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
    if (roomIsAvailable) {
      room.addBooking(bookingRequest);
      bookingRepository.save(bookingRequest);
    } else {
      throw new InvalidBookingRequestException("Xin lỗi, Phòng này không có sẵn cho những ngày đã được chọn;");
    }
    return bookingRequest.getBookingConfirmationCode();
  }

  @Override
  public BookedRoom findByBookingConfirmationCode(String confirmationCode) {
    return bookingRepository.findByBookingConfirmationCode(confirmationCode)
      .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng cho mã này: " + confirmationCode));
  }

  private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
    return existingBookings.stream()
      .noneMatch(existingBooking ->
        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
          || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
          || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
          && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
          || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

          && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
          || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

          && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

          || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
          && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

          || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
          && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
      );
  }
}
