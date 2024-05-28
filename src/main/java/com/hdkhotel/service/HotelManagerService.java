package com.hdkhotel.service;

import com.hdkhotel.model.HotelManager;
import com.hdkhotel.model.User;

public interface HotelManagerService {
  HotelManager findByUser(User user);
}
