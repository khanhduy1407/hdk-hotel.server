package com.hdkhotel.service;

import com.hdkhotel.model.Address;
import com.hdkhotel.model.dto.AddressDTO;

public interface AddressService {
  Address saveAddress(AddressDTO addressDTO);

  AddressDTO findAddressById(Long id);

  Address updateAddress(AddressDTO addressDTO);

  void deleteAddress(Long id);

  Address mapAddressDtoToAddress(AddressDTO dto);

  AddressDTO mapAddressToAddressDto(Address address);
}
