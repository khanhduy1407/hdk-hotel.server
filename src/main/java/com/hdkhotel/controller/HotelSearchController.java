package com.hdkhotel.controller;

import com.hdkhotel.model.dto.HotelAvailabilityDTO;
import com.hdkhotel.model.dto.HotelSearchDTO;
import com.hdkhotel.service.HotelSearchService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HotelSearchController {
  private final HotelSearchService hotelSearchService;

  @GetMapping("/search")
  public String showSearchForm(@ModelAttribute("hotelSearchDTO") HotelSearchDTO hotelSearchDTO) {
    return "hotelsearch/search";
  }


  @PostMapping("/search")
  public String findAvailableHotelsByCityAndDate(@Valid @ModelAttribute("hotelSearchDTO") HotelSearchDTO hotelSearchDTO, BindingResult result) throws UnsupportedEncodingException {
    if (result.hasErrors()) {
      return "hotelsearch/search";
    }
    try {
      validateCheckinAndCheckoutDates(hotelSearchDTO.getCheckinDate(), hotelSearchDTO.getCheckoutDate());
    } catch (IllegalArgumentException e) {
      result.rejectValue("checkoutDate", null, e.getMessage());
      return "hotelsearch/search";
    }

    String encodedCity = URLEncoder.encode(hotelSearchDTO.getCity(), "UTF-8");

    // Redirect to a new GET endpoint with parameters for data fetching. Allows page refreshing
    return String.format("redirect:/search-results?city=%s&checkinDate=%s&checkoutDate=%s", encodedCity, hotelSearchDTO.getCheckinDate(), hotelSearchDTO.getCheckoutDate());
  }

  @GetMapping("/search-results")
  public String showSearchResults(@RequestParam String city, @RequestParam String checkinDate, @RequestParam String checkoutDate, Model model, RedirectAttributes redirectAttributes) {
    try {
      LocalDate parsedCheckinDate = LocalDate.parse(checkinDate);
      LocalDate parsedCheckoutDate = LocalDate.parse(checkoutDate);

      validateCheckinAndCheckoutDates(parsedCheckinDate, parsedCheckoutDate);

      log.info("Đang tìm khách sạn cho khu vực {} trong khoảng thời gian từ {} đến {}", city, checkinDate, checkoutDate);
      List<HotelAvailabilityDTO> hotels = hotelSearchService.findAvailableHotelsByCityAndDate(city, parsedCheckinDate, parsedCheckoutDate);

      if (hotels.isEmpty()) {
        model.addAttribute("noHotelsFound", true);
      }

      long durationDays = ChronoUnit.DAYS.between(parsedCheckinDate, parsedCheckoutDate);

      model.addAttribute("hotels", hotels);
      model.addAttribute("city", city);
      model.addAttribute("days", durationDays);
      model.addAttribute("checkinDate", checkinDate);
      model.addAttribute("checkoutDate", checkoutDate);

    } catch (DateTimeParseException e) {
      log.error("Định dạng ngày không hợp lệ được cung cấp cho URL tìm kiếm", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Định dạng ngày tháng không hợp lệ. Vui lòng sử dụng form tìm kiếm.");
      return "redirect:/search";
    } catch (IllegalArgumentException e) {
      log.error("Đối số không hợp lệ được cung cấp cho URL tìm kiếm", e);
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/search";
    } catch (Exception e) {
      log.error("Đã xảy ra lỗi khi đang tìm kiếm khách sạn", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
      return "redirect:/search";
    }

    return "hotelsearch/search-results";
  }

  @GetMapping("/hotel-details/{id}")
  public String showHotelDetails(@PathVariable Long id, @RequestParam String checkinDate, @RequestParam String checkoutDate, Model model, RedirectAttributes redirectAttributes) {
    try {
      LocalDate parsedCheckinDate = LocalDate.parse(checkinDate);
      LocalDate parsedCheckoutDate = LocalDate.parse(checkoutDate);

      validateCheckinAndCheckoutDates(parsedCheckinDate, parsedCheckoutDate);

      HotelAvailabilityDTO hotelAvailabilityDTO = hotelSearchService.findAvailableHotelById(id, parsedCheckinDate, parsedCheckoutDate);

      long durationDays = ChronoUnit.DAYS.between(parsedCheckinDate, parsedCheckoutDate);

      model.addAttribute("hotel", hotelAvailabilityDTO);
      model.addAttribute("durationDays", durationDays);
      model.addAttribute("checkinDate", checkinDate);
      model.addAttribute("checkoutDate", checkoutDate);

      return "hotelsearch/hotel-details";


    } catch (DateTimeParseException e) {
      log.error("Định dạng ngày được cung cấp không hợp lệ", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Định dạng ngày tháng không hợp lệ. Vui lòng sử dụng form tìm kiếm.");
      return "redirect:/search";
    } catch (IllegalArgumentException e) {
      log.error("Đối số không hợp lệ được cung cấp cho URL tìm kiếm", e);
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/search";
    } catch (EntityNotFoundException e) {
      log.error("Không tìm thấy khách sạn nào có ID {}", id);
      redirectAttributes.addFlashAttribute("errorMessage", "Khách sạn đã chọn không còn tồn tại. Hãy bắt đầu một tìm kiếm mới.");
      return "redirect:/search";
    } catch (Exception e) {
      log.error("Đã xảy ra lỗi khi tìm kiếm khách sạn", e);
      redirectAttributes.addFlashAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn. Vui lòng thử lại sau.");
      return "redirect:/search";
    }
  }

  private void validateCheckinAndCheckoutDates(LocalDate checkinDate, LocalDate checkoutDate) {
    if (checkinDate.isBefore(LocalDate.now())) {
      throw new IllegalArgumentException("Ngày nhận phòng không được là ngày trong quá khứ");
    }
    if (checkoutDate.isBefore(checkinDate.plusDays(1))) {
      throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng");
    }
  }

  private void parseAndValidateBookingDates(String checkinDate, String checkoutDate) {
    LocalDate parsedCheckinDate = LocalDate.parse(checkinDate);
    LocalDate parsedCheckoutDate = LocalDate.parse(checkoutDate);
    validateCheckinAndCheckoutDates(parsedCheckinDate, parsedCheckoutDate);
  }
}
