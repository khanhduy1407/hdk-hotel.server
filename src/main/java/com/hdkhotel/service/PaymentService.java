package com.hdkhotel.service;

import com.hdkhotel.model.Booking;
import com.hdkhotel.model.Payment;
import com.hdkhotel.model.dto.BookingInitiationDTO;

public interface PaymentService {
  Payment savePayment(BookingInitiationDTO bookingInitiationDTO, Booking booking);
}
