package com.hdkhotel.service.impl;

import com.hdkhotel.model.Booking;
import com.hdkhotel.model.Payment;
import com.hdkhotel.model.dto.BookingInitiationDTO;
import com.hdkhotel.model.enums.Currency;
import com.hdkhotel.model.enums.PaymentMethod;
import com.hdkhotel.model.enums.PaymentStatus;
import com.hdkhotel.repository.PaymentRepository;
import com.hdkhotel.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepository paymentRepository;

  @Override
  public Payment savePayment(BookingInitiationDTO bookingInitiationDTO, Booking booking) {
    Payment payment = Payment.builder()
      .booking(booking)
      .totalPrice(bookingInitiationDTO.getTotalPrice())
      .paymentStatus(PaymentStatus.COMPLETED) // Assuming the payment is completed
      .paymentMethod(PaymentMethod.CREDIT_CARD) // Default to CREDIT_CARD
      .currency(Currency.USD) // Default to USD
      .build();

    Payment savedPayment = paymentRepository.save(payment);
    log.info("Thanh toán được lưu với ID giao dịch: {}", savedPayment.getTransactionId());

    return savedPayment;
  }
}
