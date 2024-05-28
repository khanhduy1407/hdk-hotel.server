package com.hdkhotel.repository;

import com.hdkhotel.model.HotelManager;
import com.hdkhotel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelManagerRepository extends JpaRepository<HotelManager, Long> {
  Optional<HotelManager> findByUser(User user);
}
