package com.hdkhotel.initialize;

import com.hdkhotel.model.*;
import com.hdkhotel.model.enums.RoleType;
import com.hdkhotel.model.enums.RoomType;
import com.hdkhotel.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataInitializer implements CommandLineRunner {
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AdminRepository adminRepository;
  private final CustomerRepository customerRepository;
  private final HotelManagerRepository hotelManagerRepository;
  private final PasswordEncoder passwordEncoder;
  private final AddressRepository addressRepository;
  private final HotelRepository hotelRepository;
  private final AvailabilityRepository availabilityRepository;

  @Override
  @Transactional
  public void run(String... args) {
    try {
      log.warn("Đang kiểm tra xem có cần phải duy trì dữ liệu test hay không...");

      if (roleRepository.count() == 0 && userRepository.count() == 0) {
        log.info("Bắt đầu lưu dữ liệu test");

        Role adminRole = new Role(RoleType.ADMIN);
        Role customerRole = new Role(RoleType.CUSTOMER);
        Role hotelManagerRole = new Role(RoleType.HOTEL_MANAGER);

        roleRepository.save(adminRole);
        roleRepository.save(customerRole);
        roleRepository.save(hotelManagerRole);
        log.info("Dữ liệu \"role\" vẫn tồn tại");

        User user1 = User.builder().username("admin@hotel.com").password(passwordEncoder.encode("123456")).name("Admin").lastName("Admin").role(adminRole).build();
        User user2 = User.builder().username("customer1@hotel.com").password(passwordEncoder.encode("123456")).name("Tấn Dũng").lastName("Nguyễn").role(customerRole).build();
        User user3 = User.builder().username("manager1@hotel.com").password(passwordEncoder.encode("123456")).name("Gia Kiệt").lastName("Trần").role(hotelManagerRole).build();
        User user4 = User.builder().username("manager2@hotel.com").password(passwordEncoder.encode("123456")).name("Thịnh Hưng").lastName("Hứa").role(hotelManagerRole).build();

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        userRepository.save(user4);

        Admin admin1 = Admin.builder().user(user1).build();
        Customer c1 = Customer.builder().user(user2).build();
        HotelManager hm1 = HotelManager.builder().user(user3).build();
        HotelManager hm2 = HotelManager.builder().user(user4).build();

        adminRepository.save(admin1);
        customerRepository.save(c1);
        hotelManagerRepository.save(hm1);
        hotelManagerRepository.save(hm2);
        log.info("Dữ liệu \"user\" vẫn tồn tại");

        Address addressIst1 = Address.builder().addressLine("Khu Công nghệ cao Xa Lộ Hà Nội, Hiệp Phú, Thủ Đức, Thành phố Hồ Chí Minh, Vietnam")
          .city("Hồ Chí Minh").country("Vietnam").build();
        Address addressIst2 = Address.builder().addressLine("54 P. Liễu Giai, Cống Vị, Ba Đình, Hà Nội 100000, Vietnam")
          .city("Hà Nội").country("Vietnam").build();
        Address addressIst3 = Address.builder().addressLine("19 Trần Kim Hùng, Vĩnh Thọ, Nha Trang, Khánh Hòa 650000, Vietnam")
          .city("Nha Trang").country("Vietnam").build();

        Address addressBerlin1 = Address.builder().addressLine("Unter den Linden 77").city("Berlin")
          .country("Germany").build();
        Address addressBerlin2 = Address.builder().addressLine("Potsdamer Platz 3, Mitte, 10785").city("Berlin")
          .country("Germany").build();
        Address addressBerlin3 = Address.builder().addressLine("Budapester Str. 2, Mitte, 10787").city("Berlin")
          .country("Germany").build();

        addressRepository.save(addressIst1);
        addressRepository.save(addressIst2);
        addressRepository.save(addressIst3);
        addressRepository.save(addressBerlin1);
        addressRepository.save(addressBerlin2);
        addressRepository.save(addressBerlin3);

        Hotel hotelIst1 = Hotel.builder().name("HUTECH Thu Duc Campus")
          .address(addressIst1).hotelManager(hm1).build();
        Hotel hotelIst2 = Hotel.builder().name("Hanoi Hotel")
          .address(addressIst2).hotelManager(hm1).build();
        Hotel hotelIst3 = Hotel.builder().name("Softly Hotel")
          .address(addressIst3).hotelManager(hm1).build();

        Hotel hotelBerlin1 = Hotel.builder().name("Hotel Adlon Kempinski Berlin")
          .address(addressBerlin1).hotelManager(hm2).build();
        Hotel hotelBerlin2 = Hotel.builder().name("The Ritz-Carlton Berlin")
          .address(addressBerlin2).hotelManager(hm2).build();
        Hotel hotelBerlin3 = Hotel.builder().name("InterContinental Berlin")
          .address(addressBerlin3).hotelManager(hm2).build();

        Room singleRoomIst1 = Room.builder().roomType(RoomType.SINGLE)
          .pricePerNight(370).roomCount(35).hotel(hotelIst1).build();
        Room doubleRoomIst1 = Room.builder().roomType(RoomType.DOUBLE)
          .pricePerNight(459).roomCount(45).hotel(hotelIst1).build();

        Room singleRoomIst2 = Room.builder().roomType(RoomType.SINGLE)
          .pricePerNight(700).roomCount(25).hotel(hotelIst2).build();
        Room doubleRoomIst2 = Room.builder().roomType(RoomType.DOUBLE)
          .pricePerNight(890).roomCount(30).hotel(hotelIst2).build();

        Room singleRoomIst3 = Room.builder().roomType(RoomType.SINGLE)
          .pricePerNight(691).roomCount(30).hotel(hotelIst3).build();
        Room doubleRoomIst3 = Room.builder().roomType(RoomType.DOUBLE)
          .pricePerNight(800).roomCount(75).hotel(hotelIst3).build();

        Room singleRoomBerlin1 = Room.builder().roomType(RoomType.SINGLE)
          .pricePerNight(120.0).roomCount(25).hotel(hotelBerlin1).build();
        Room doubleRoomBerlin1 = Room.builder().roomType(RoomType.DOUBLE)
          .pricePerNight(250.0).roomCount(15).hotel(hotelBerlin1).build();

        Room singleRoomBerlin2 = Room.builder().roomType(RoomType.SINGLE)
          .pricePerNight(300).roomCount(50).hotel(hotelBerlin2).build();
        Room doubleRoomBerlin2 = Room.builder().roomType(RoomType.DOUBLE)
          .pricePerNight(400).roomCount(50).hotel(hotelBerlin2).build();

        Room singleRoomBerlin3 = Room.builder().roomType(RoomType.SINGLE)
          .pricePerNight(179).roomCount(45).hotel(hotelBerlin3).build();
        Room doubleRoomBerlin3 = Room.builder().roomType(RoomType.DOUBLE)
          .pricePerNight(256).roomCount(25).hotel(hotelBerlin3).build();

        hotelIst1.getRooms().addAll(Arrays.asList(singleRoomIst1,doubleRoomIst1));
        hotelIst2.getRooms().addAll(Arrays.asList(singleRoomIst2,doubleRoomIst2));
        hotelIst3.getRooms().addAll(Arrays.asList(singleRoomIst3,doubleRoomIst3));
        hotelBerlin1.getRooms().addAll(Arrays.asList(singleRoomBerlin1,doubleRoomBerlin1));
        hotelBerlin2.getRooms().addAll(Arrays.asList(singleRoomBerlin2,doubleRoomBerlin2));
        hotelBerlin3.getRooms().addAll(Arrays.asList(singleRoomBerlin3,doubleRoomBerlin3));

        hotelRepository.save(hotelIst1);
        hotelRepository.save(hotelIst2);
        hotelRepository.save(hotelIst3);
        hotelRepository.save(hotelBerlin1);
        hotelRepository.save(hotelBerlin2);
        hotelRepository.save(hotelBerlin3);
        log.info("Dữ liệu \"hotel\" vẫn tồn tại");

        Availability av1Berlin1 = Availability.builder().hotel(hotelBerlin1)
          .date(LocalDate.of(2023,9,1)).room(singleRoomBerlin1).availableRooms(5).build();
        Availability av2Berlin1 = Availability.builder().hotel(hotelBerlin1)
          .date(LocalDate.of(2023,9,2)).room(doubleRoomBerlin1).availableRooms(7).build();

        availabilityRepository.save(av1Berlin1);
        availabilityRepository.save(av2Berlin1);
        log.info("Dữ liệu \"availability\" vẫn tồn tại");

      } else {
        log.info("Kiểm tra duy trì dữ liệu test là không cần thiết");
      }
      log.warn("Ứng dụng đã sẵn sàng");
    } catch (DataAccessException e) {
      log.error("Ngoại lệ xảy ra trong quá trình lưu giữ dữ liệu: " + e.getMessage());
    } catch (Exception e) {
      log.error("Đã xảy ra ngoại lệ không mong đợi: " + e.getMessage());
    }
  }
}
