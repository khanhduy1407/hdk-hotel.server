package com.hdkhotel.service.impl;

import com.hdkhotel.model.HotelManager;
import com.hdkhotel.model.User;
import com.hdkhotel.repository.HotelManagerRepository;
import com.hdkhotel.service.HotelManagerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelManagerServiceImpl implements HotelManagerService {
  private final HotelManagerRepository hotelManagerRepository;

  @Override
  public HotelManager findByUser(User user) {
    log.info("Đang cố gắng tìm HotelManager cho ID người dùng: {}", user.getId());
    return hotelManagerRepository.findByUser(user)
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy HotelManager cho người dùng " + user.getUsername()));
  }
}
