package com.hdkhotel.model;

import java.time.LocalDate;

public class BookedRoom {
  private Long bookingId;
  private LocalDate checkInDate;
  private LocalDate checkOutDate;
  private String guestFullName;
  private String guestEmail;
  private int NumOfAdults;
  private int NumOfChildren;
  private int totalNumOfGuest;
}
