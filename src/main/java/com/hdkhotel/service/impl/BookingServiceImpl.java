package com.hdkhotel.service.impl;

import com.hdkhotel.model.*;
import com.hdkhotel.model.dto.AddressDTO;
import com.hdkhotel.model.dto.BookingDTO;
import com.hdkhotel.model.dto.BookingInitiationDTO;
import com.hdkhotel.model.dto.RoomSelectionDTO;
import com.hdkhotel.repository.BookingRepository;
import com.hdkhotel.service.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
  private final BookingRepository bookingRepository;
  private final AvailabilityService availabilityService;
  private final PaymentService paymentService;
  private final CustomerService customerService;
  private final HotelService hotelService;

  @Override
  @Transactional
  public Booking saveBooking(BookingInitiationDTO bookingInitiationDTO, Long userId) {
    validateBookingDates(bookingInitiationDTO.getCheckinDate(), bookingInitiationDTO.getCheckoutDate());

    Customer customer = customerService.findByUserId(userId)
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách hàng có ID người dùng: " + userId));

    Hotel hotel = hotelService.findHotelById(bookingInitiationDTO.getHotelId())
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy khách sạn có ID: " + bookingInitiationDTO.getHotelId()));

    Booking booking = mapBookingInitDtoToBookingModel(bookingInitiationDTO, customer, hotel);

    return bookingRepository.save(booking);
  }

  @Override
  @Transactional
  public BookingDTO confirmBooking(BookingInitiationDTO bookingInitiationDTO, Long customerId) {
    Booking savedBooking = this.saveBooking(bookingInitiationDTO, customerId);
    Payment savedPayment = paymentService.savePayment(bookingInitiationDTO, savedBooking);
    savedBooking.setPayment(savedPayment);
    bookingRepository.save(savedBooking);
    availabilityService.updateAvailabilities(bookingInitiationDTO.getHotelId(), bookingInitiationDTO.getCheckinDate(),
      bookingInitiationDTO.getCheckoutDate(), bookingInitiationDTO.getRoomSelections());
    return mapBookingModelToBookingDto(savedBooking);
  }

  @Override
  public List<BookingDTO> findAllBookings() {
    List<Booking> bookings = bookingRepository.findAll();
    return bookings.stream()
      .map(this::mapBookingModelToBookingDto)
      .collect(Collectors.toList());
  }

  @Override
  public BookingDTO findBookingById(Long bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt phòng với ID: " + bookingId));
    return mapBookingModelToBookingDto(booking);
  }

  @Override
  public List<BookingDTO> findBookingsByCustomerId(Long customerId) {
    List<Booking> bookingDTOs = bookingRepository.findBookingsByCustomerId(customerId);
    return bookingDTOs.stream()
      .map(this::mapBookingModelToBookingDto)
      .sorted(Comparator.comparing(BookingDTO::getCheckinDate))
      .collect(Collectors.toList());
  }

  @Override
  public BookingDTO findBookingByIdAndCustomerId(Long bookingId, Long customerId) {
    Booking booking = bookingRepository.findBookingByIdAndCustomerId(bookingId, customerId)
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt phòng với ID: " + bookingId));
    return mapBookingModelToBookingDto(booking);
  }

  @Override
  public List<BookingDTO> findBookingsByManagerId(Long managerId) {
    List<Hotel> hotels = hotelService.findAllHotelsByManagerId(managerId);
    return hotels.stream()
      .flatMap(hotel -> bookingRepository.findBookingsByHotelId(hotel.getId()).stream())
      .map(this::mapBookingModelToBookingDto)
      .collect(Collectors.toList());
  }

  @Override
  public BookingDTO findBookingByIdAndManagerId(Long bookingId, Long managerId) {
    Booking booking = bookingRepository.findBookingByIdAndHotel_HotelManagerId(bookingId, managerId)
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thông tin đặt phòng với ID: " + bookingId + " và ID người quản lý: " + managerId));
    return mapBookingModelToBookingDto(booking);
  }

  private void validateBookingDates(LocalDate checkinDate, LocalDate checkoutDate) {
    if (checkinDate.isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("Ngày nhận phòng không được là ngày trong quá khứ");
    }
    if (checkoutDate.isBefore(checkinDate.plusDays(1))) {
      throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng");
    }
  }

  @Override
  public BookingDTO mapBookingModelToBookingDto(Booking booking) {
    AddressDTO addressDto = AddressDTO.builder()
      .addressLine(booking.getHotel().getAddress().getAddressLine())
      .city(booking.getHotel().getAddress().getCity())
      .country(booking.getHotel().getAddress().getCountry())
      .build();

    List<RoomSelectionDTO> roomSelections = booking.getBookedRooms().stream()
      .map(room -> RoomSelectionDTO.builder()
        .roomType(room.getRoomType())
        .count(room.getCount())
        .build())
      .collect(Collectors.toList());

    User customerUser = booking.getCustomer().getUser();

    return BookingDTO.builder()
      .id(booking.getId())
      .confirmationNumber(booking.getConfirmationNumber())
      .bookingDate(booking.getBookingDate())
      .customerId(booking.getCustomer().getId())
      .hotelId(booking.getHotel().getId())
      .checkinDate(booking.getCheckinDate())
      .checkoutDate(booking.getCheckoutDate())
      .roomSelections(roomSelections)
      .totalPrice(booking.getPayment().getTotalPrice())
      .hotelName(booking.getHotel().getName())
      .hotelAddress(addressDto)
      .customerName(customerUser.getName() + " " + customerUser.getLastName())
      .customerEmail(customerUser.getUsername())
      .paymentStatus(booking.getPayment().getPaymentStatus())
      .paymentMethod(booking.getPayment().getPaymentMethod())
      .build();
  }

  private Booking mapBookingInitDtoToBookingModel(BookingInitiationDTO bookingInitiationDTO, Customer customer, Hotel hotel) {
    Booking booking = Booking.builder()
      .customer(customer)
      .hotel(hotel)
      .checkinDate(bookingInitiationDTO.getCheckinDate())
      .checkoutDate(bookingInitiationDTO.getCheckoutDate())
      .build();

    for (RoomSelectionDTO roomSelection : bookingInitiationDTO.getRoomSelections()) {
      if (roomSelection.getCount() > 0) {
        BookedRoom bookedRoom = BookedRoom.builder()
          .booking(booking)
          .roomType(roomSelection.getRoomType())
          .count(roomSelection.getCount())
          .build();
        booking.getBookedRooms().add(bookedRoom);
      }
    }

    return booking;
  }
}
