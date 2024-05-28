package com.hdkhotel.service.impl;

import com.hdkhotel.model.Customer;
import com.hdkhotel.repository.CustomerRepository;
import com.hdkhotel.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {
  private final CustomerRepository customerRepository;

  @Override
  public Optional<Customer> findByUserId(Long userId) {
    return customerRepository.findByUserId(userId);
  }

  @Override
  public Optional<Customer> findByUsername(String username) {
    return customerRepository.findByUsername(username);
  }
}
