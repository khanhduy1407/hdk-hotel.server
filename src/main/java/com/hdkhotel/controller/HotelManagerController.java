package com.hdkhotel.controller;

import com.hdkhotel.exception.HotelAlreadyExistsException;
import com.hdkhotel.model.dto.BookingDTO;
import com.hdkhotel.model.enums.RoomType;
import com.hdkhotel.model.dto.HotelDTO;
import com.hdkhotel.model.dto.HotelRegistrationDTO;
import com.hdkhotel.model.dto.RoomDTO;
import com.hdkhotel.service.BookingService;
import com.hdkhotel.service.HotelService;
import com.hdkhotel.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
@Slf4j
public class HotelManagerController {
  private final HotelService hotelService;
  private final UserService userService;
  private final BookingService bookingService;

  @GetMapping("/dashboard")
  public String dashboard() {
    return "hotelmanager/dashboard";
  }

  @GetMapping("/hotels/add")
  public String showAddHotelForm(Model model) {
    HotelRegistrationDTO hotelRegistrationDTO = new HotelRegistrationDTO();

    // Initialize roomDTOs with SINGLE and DOUBLE room types
    RoomDTO singleRoom = new RoomDTO(null, null, RoomType.SINGLE, 0, 0.0);
    RoomDTO doubleRoom = new RoomDTO(null, null, RoomType.DOUBLE, 0, 0.0);
    hotelRegistrationDTO.getRoomDTOs().add(singleRoom);
    hotelRegistrationDTO.getRoomDTOs().add(doubleRoom);

    model.addAttribute("hotel", hotelRegistrationDTO);
    return "hotelmanager/hotels-add";
  }

  @PostMapping("/hotels/add")
  public String addHotel(@Valid @ModelAttribute("hotel") HotelRegistrationDTO hotelRegistrationDTO, BindingResult result, RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      log.warn("Tạo khách sạn không thành công do lỗi xác thực: {}", result.getAllErrors());
      return "hotelmanager/hotels-add";
    }
    try {
      hotelService.saveHotel(hotelRegistrationDTO);
      redirectAttributes.addFlashAttribute("message", "Đã thêm khách sạn (" + hotelRegistrationDTO.getName() + ") thành công");
      return "redirect:/manager/hotels";
    } catch (HotelAlreadyExistsException e) {
      result.rejectValue("name", "hotel.exists", e.getMessage());
      return "hotelmanager/hotels-add";
    }
  }

  @GetMapping("/hotels")
  public String listHotels(Model model) {
    Long managerId = getCurrentManagerId();
    List<HotelDTO> hotelList = hotelService.findAllHotelDtosByManagerId(managerId);
    model.addAttribute("hotels", hotelList);
    return "hotelmanager/hotels";
  }

  @GetMapping("/hotels/edit/{id}")
  public String showEditHotelForm(@PathVariable Long id, Model model) {
    Long managerId = getCurrentManagerId();
    HotelDTO hotelDTO = hotelService.findHotelByIdAndManagerId(id, managerId);
    model.addAttribute("hotel", hotelDTO);
    return "hotelmanager/hotels-edit";
  }

  @PostMapping("/hotels/edit/{id}")
  public String editHotel(@PathVariable Long id, @Valid @ModelAttribute("hotel") HotelDTO hotelDTO, BindingResult result, RedirectAttributes redirectAttributes) {
    if (result.hasErrors()) {
      return "hotelmanager/hotels-edit";
    }
    try {
      Long managerId = getCurrentManagerId();
      hotelDTO.setId(id);
      hotelService.updateHotelByManagerId(hotelDTO, managerId);
      redirectAttributes.addFlashAttribute("message", "Đã cập nhật khách sạn (ID: " + id + ") thành công");
      return "redirect:/manager/hotels";

    } catch (HotelAlreadyExistsException e) {
      result.rejectValue("name", "hotel.exists", e.getMessage());
      return "hotelmanager/hotels-edit";
    } catch (EntityNotFoundException e) {
      result.rejectValue("id", "hotel.notfound", e.getMessage());
      return "hotelmanager/hotels-edit";
    }
  }

  @PostMapping("/hotels/delete/{id}")
  public String deleteHotel(@PathVariable Long id) {
    Long managerId = getCurrentManagerId();
    hotelService.deleteHotelByIdAndManagerId(id, managerId);
    return "redirect:/manager/hotels";
  }

  @GetMapping("/bookings")
  public String listBookings(Model model, RedirectAttributes redirectAttributes) {
    try {
      Long managerId = getCurrentManagerId();
      List<BookingDTO> bookingDTOs = bookingService.findBookingsByManagerId(managerId);
      model.addAttribute("bookings", bookingDTOs);

      return "hotelmanager/bookings";
    } catch (EntityNotFoundException e) {
      log.error("Không tìm thấy thông tin đặt phòng nào cho ID người quản lý được cung cấp", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin đặt phòng. Vui lòng thử lại sau.");
      return "redirect:/manager/dashboard";
    } catch (Exception e) {
      log.error("Đã xảy ra lỗi khi liệt kê danh sách thông tin đặt phòng", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
      return "redirect:/manager/dashboard";
    }
  }

  @GetMapping("/bookings/{id}")
  public String viewBookingDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
      Long managerId = getCurrentManagerId();
      BookingDTO bookingDTO = bookingService.findBookingByIdAndManagerId(id, managerId);
      model.addAttribute("bookingDTO", bookingDTO);

      LocalDate checkinDate = bookingDTO.getCheckinDate();
      LocalDate checkoutDate = bookingDTO.getCheckoutDate();
      long durationDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
      model.addAttribute("days", durationDays);

      return "hotelmanager/bookings-details";
    } catch (EntityNotFoundException e) {
      log.error("Không tìm thấy thông tin đặt phòng nào với ID được cung cấp", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin đặt phòng. Vui lòng thử lại sau.");
      return "redirect:/manager/dashboard";
    } catch (Exception e) {
      log.error("Đã xảy ra lỗi khi hiển thị chi tiết thông tin đặt phòng", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
      return "redirect:/manager/dashboard";
    }
  }

  private Long getCurrentManagerId() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userService.findUserByUsername(username).getHotelManager().getId();
  }
}
