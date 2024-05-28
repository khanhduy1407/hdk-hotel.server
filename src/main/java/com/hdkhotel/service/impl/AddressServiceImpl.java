package com.hdkhotel.service.impl;

import com.hdkhotel.model.Address;
import com.hdkhotel.model.dto.AddressDTO;
import com.hdkhotel.repository.AddressRepository;
import com.hdkhotel.service.AddressService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {
  private final AddressRepository addressRepository;

  @Override
  public Address saveAddress(AddressDTO addressDTO) {
    log.info("Đang cố gắng lưu một địa chỉ mới: {}", addressDTO.toString());
    Address address = mapAddressDtoToAddress(addressDTO);

    Address savedAddress = addressRepository.save(address);
    log.info("Đã lưu thành công địa chỉ mới với ID: {}", address.getId());
    return savedAddress;
  }

  @Override
  public AddressDTO findAddressById(Long id) {
    Address address = addressRepository.findById(id)
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ"));

    return mapAddressToAddressDto(address);
  }

  @Override
  public Address updateAddress(AddressDTO addressDTO) {
    log.info("Đang cố gắng cập nhật địa chỉ với ID: {}", addressDTO.getId());
    Address existingAddress = addressRepository.findById(addressDTO.getId())
      .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy địa chỉ"));

    setFormattedDataToAddress(existingAddress, addressDTO);

    Address updatedAddress = addressRepository.save(existingAddress);
    log.info("Đã cập nhật thành công địa chỉ với ID: {}", existingAddress.getId());
    return updatedAddress;
  }

  @Override
  public void deleteAddress(Long id) {
    log.info("Đang cố gắng xóa địa chỉ với ID: {}", id);
    if (!addressRepository.existsById(id)) {
      log.error("Không thể xóa địa chỉ. Địa chỉ với ID: {} không tìm thấy", id);
      throw new EntityNotFoundException("Không tìm thấy địa chỉ");
    }
    addressRepository.deleteById(id);
    log.info("Đã xóa thành công địa chỉ với ID: {}", id);
  }

  @Override
  public Address mapAddressDtoToAddress(AddressDTO dto) {
    return Address.builder()
      .addressLine(formatText(dto.getAddressLine()))
      .city(formatText(dto.getCity()))
      .country(formatText(dto.getCountry()))
      .build();
  }

  @Override
  public AddressDTO mapAddressToAddressDto(Address address) {
    return AddressDTO.builder()
      .id(address.getId())
      .addressLine(address.getAddressLine())
      .city(address.getCity())
      .country(address.getCountry())
      .build();
  }

  private String formatText(String text) {
    return StringUtils.capitalize(text.trim());
  }

  private void setFormattedDataToAddress(Address address, AddressDTO addressDTO) {
    address.setAddressLine(formatText(addressDTO.getAddressLine()));
    address.setCity(formatText(addressDTO.getCity()));
    address.setCountry(formatText(addressDTO.getCountry()));
  }
}
