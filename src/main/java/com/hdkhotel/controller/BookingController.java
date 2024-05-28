package com.hdkhotel.controller;

import com.hdkhotel.model.dto.*;
import com.hdkhotel.service.BookingService;
import com.hdkhotel.service.HotelService;
import com.hdkhotel.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/booking")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
  private final HotelService hotelService;
  private final UserService userService;
  private final BookingService bookingService;

  @PostMapping("/initiate")
  public String initiateBooking(@ModelAttribute BookingInitiationDTO bookingInitiationDTO, HttpSession session) {
    session.setAttribute("bookingInitiationDTO", bookingInitiationDTO);
    log.debug("BookingInitiationDTO được đặt trong phiên: {}", bookingInitiationDTO);
    return "redirect:/booking/payment";
  }

  @GetMapping("/payment")
  public String showPaymentPage(Model model, RedirectAttributes redirectAttributes, HttpSession session) {
    BookingInitiationDTO bookingInitiationDTO = (BookingInitiationDTO) session.getAttribute("bookingInitiationDTO");
    log.debug("BookingInitiationDTO được truy xuất từ phiên: {}", bookingInitiationDTO);

    if (bookingInitiationDTO == null) {
      redirectAttributes.addFlashAttribute("errorMessage", "Phiên của bạn đã hết hạn. Hãy bắt đầu một tìm kiếm mới.");
      return "redirect:/search";
    }

    HotelDTO hotelDTO = hotelService.findHotelDtoById(bookingInitiationDTO.getHotelId());

    model.addAttribute("bookingInitiationDTO", bookingInitiationDTO);
    model.addAttribute("hotelDTO", hotelDTO);
    model.addAttribute("paymentCardDTO", new PaymentCardDTO());

    return "booking/payment";
  }

  @PostMapping("/payment")
  public String confirmBooking(@Valid @ModelAttribute("paymentCardDTO") PaymentCardDTO paymentDTO, BindingResult result, Model model, HttpSession session, RedirectAttributes redirectAttributes) {
    BookingInitiationDTO bookingInitiationDTO = (BookingInitiationDTO) session.getAttribute("bookingInitiationDTO");
    log.debug("BookingInitiationDTO được truy xuất từ phiên khi bắt đầu completeBooking: {}", bookingInitiationDTO);
    if (bookingInitiationDTO == null) {
      redirectAttributes.addFlashAttribute("errorMessage", "Phiên của bạn đã hết hạn. Hãy bắt đầu một tìm kiếm mới.");
      return "redirect:/search";
    }

    if (result.hasErrors()) {
      log.warn("Đã xảy ra lỗi xác thực khi đang hoàn tất đặt phòng");
      HotelDTO hotelDTO = hotelService.findHotelDtoById(bookingInitiationDTO.getHotelId());
      model.addAttribute("bookingInitiationDTO", bookingInitiationDTO);
      model.addAttribute("hotelDTO", hotelDTO);
      model.addAttribute("paymentCardDTO", paymentDTO);
      return "booking/payment";
    }

    try {
      Long userId = getLoggedInUserId();
      BookingDTO bookingDTO = bookingService.confirmBooking(bookingInitiationDTO, userId);
      redirectAttributes.addFlashAttribute("bookingDTO", bookingDTO);

      return "redirect:/booking/confirmation";
    } catch (Exception e) {
      log.error("Đã xảy ra lỗi khi đang hoàn tất đặt phòng", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
      return "redirect:/booking/payment";
    }
  }

  @GetMapping("/confirmation")
  public String showConfirmationPage(Model model, RedirectAttributes redirectAttributes) {
    // Attempt to retrieve the bookingDTO from flash attributes
    BookingDTO bookingDTO = (BookingDTO) model.asMap().get("bookingDTO");

    if (bookingDTO == null) {
      redirectAttributes.addFlashAttribute("errorMessage", "Phiên của bạn đã hết hạn hoặc quá trình đặt phòng không được hoàn thành đúng cách. Hãy bắt đầu một tìm kiếm mới.");
      return "redirect:/search";
    }

    LocalDate checkinDate = bookingDTO.getCheckinDate();
    LocalDate checkoutDate = bookingDTO.getCheckoutDate();
    long durationDays = ChronoUnit.DAYS.between(checkinDate, checkoutDate);

    model.addAttribute("days", durationDays);
    model.addAttribute("bookingDTO", bookingDTO);

    return "booking/confirmation";
  }

  private Long getLoggedInUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    UserDTO userDTO = userService.findUserDTOByUsername(username);
    log.info("Đã tìm nạp ID người dùng đã đăng nhập: {}", userDTO.getId());
    return userDTO.getId();
  }

}
