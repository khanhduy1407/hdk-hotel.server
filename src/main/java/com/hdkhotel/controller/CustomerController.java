package com.hdkhotel.controller;

import com.hdkhotel.model.dto.BookingDTO;
import com.hdkhotel.service.BookingService;
import com.hdkhotel.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequestMapping("/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerController {
  private final UserService userService;
  private final BookingService bookingService;

  @GetMapping("/dashboard")
  public String dashboard() {
    return "customer/dashboard";
  }

  @GetMapping("/bookings")
  public String listBookings(Model model, RedirectAttributes redirectAttributes) {
    try {
      Long customerId = getCurrentCustomerId();
      List<BookingDTO> bookingDTOs = bookingService.findBookingsByCustomerId(customerId);
      model.addAttribute("bookings", bookingDTOs);
      return "customer/bookings";
    } catch (EntityNotFoundException e) {
      log.error("Không tìm thấy khách hàng nào có ID được cung cấp", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy khách hàng. Vui lòng đăng nhập lại.");
      return "redirect:/login";
    } catch (Exception e) {
      log.error("Đã xảy ra lỗi khi liệt kê danh sách thông tin đặt phòng", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
      return "redirect:/";
    }
  }

  @GetMapping("/bookings/{id}")
  public String viewBookingDetails(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
    try {
      Long customerId = getCurrentCustomerId();
      BookingDTO bookingDTO = bookingService.findBookingByIdAndCustomerId(id, customerId);
      model.addAttribute("bookingDTO", bookingDTO);

      LocalDate checkinDate = bookingDTO.getCheckinDate();
      LocalDate checkoutDate = bookingDTO.getCheckoutDate();
      long durationDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);
      model.addAttribute("days", durationDays);

      return "customer/bookings-details";
    } catch (EntityNotFoundException e) {
      log.error("Không tìm thấy thông tin đặt phòng nào với ID được cung cấp", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thông tin đặt phòng. Vui lòng thử lại sau.");
      return "redirect:/customer/bookings";
    } catch (Exception e) {
      log.error("Đã xảy ra lỗi khi hiển thị chi tiết thông tin đặt phòng", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
      return "redirect:/";
    }
  }

  private Long getCurrentCustomerId() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userService.findUserByUsername(username).getCustomer().getId();
  }
}
