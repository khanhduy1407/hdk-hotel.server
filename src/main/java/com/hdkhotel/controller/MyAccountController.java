package com.hdkhotel.controller;

import com.hdkhotel.exception.UsernameAlreadyExistsException;
import com.hdkhotel.model.dto.UserDTO;
import com.hdkhotel.service.UserService;
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

@Controller
@RequiredArgsConstructor
@Slf4j
public class MyAccountController {
  private final UserService userService;

  // Customer actions
  @GetMapping("/customer/account")
  public String showCustomerAccount(Model model){
    log.debug("Đang hiển thị tài khoản khách hàng");
    addLoggedInUserDataToModel(model);
    return "customer/account";
  }

  @GetMapping("/customer/account/edit")
  public String showCustomerEditForm(Model model){
    log.debug("Đang hiển thị form chỉnh sửa tài khoản khách hàng");
    addLoggedInUserDataToModel(model);
    return "customer/account-edit";
  }

  @PostMapping("/customer/account/edit")
  public String editCustomerAccount(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result) {
    log.info("Đang cố gắng chỉnh sửa chi tiết tài khoản khách hàng cho ID: {}", userDTO.getId());
    if (result.hasErrors()) {
      log.warn("Đã xảy ra lỗi xác thực khi chỉnh sửa tài khoản khách hàng");
      return "customer/account-edit";
    }
    try {
      userService.updateLoggedInUser(userDTO);
      log.info("Đã chỉnh sửa thành công tài khoản khách hàng");
    } catch (UsernameAlreadyExistsException e) {
      log.error("Lỗi tên người dùng đã tồn tại", e);
      result.rejectValue("username", "user.exists", "Tên người dùng đã được đăng ký!");
      return "customer/account-edit";
    }
    return "redirect:/customer/account?success";
  }

  // Hotel Manager actions
  @GetMapping("/manager/account")
  public String showHotelManagerAccount(Model model){
    log.debug("Đang hiển thị tài khoản quản lý khách sạn");
    addLoggedInUserDataToModel(model);
    return "hotelmanager/account";
  }

  @GetMapping("/manager/account/edit")
  public String showHotelManagerEditForm(Model model){
    log.debug("Đang hiển thị form chỉnh sửa tài khoản người quản lý khách sạn");
    addLoggedInUserDataToModel(model);
    return "hotelmanager/account-edit";
  }

  @PostMapping("/manager/account/edit")
  public String editHotelManagerAccount(@Valid @ModelAttribute("user") UserDTO userDTO, BindingResult result) {
    log.info("Đang cố gắng chỉnh sửa chi tiết tài khoản người quản lý khách sạn cho ID: {}", userDTO.getId());
    if (result.hasErrors()) {
      log.warn("Đã xảy ra lỗi xác thực khi chỉnh sửa tài khoản người quản lý khách sạn");
      return "hotelmanager/account-edit";
    }
    try {
      userService.updateLoggedInUser(userDTO);
      log.info("Đã chỉnh sửa thành công tài khoản quản lý khách sạn");
    } catch (UsernameAlreadyExistsException e) {
      log.error("Lỗi tên người dùng đã tồn tại", e);
      result.rejectValue("username", "user.exists", "Tên người dùng đã được đăng ký!");
      return "hotelmanager/account-edit";
    }
    return "redirect:/manager/account?success";
  }

  private void addLoggedInUserDataToModel(Model model) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    UserDTO userDTO = userService.findUserDTOByUsername(username);
    log.info("Thêm dữ liệu người dùng đã đăng nhập vào mô hình cho ID người dùng: {}", userDTO.getId());
    model.addAttribute("user", userDTO);
  }
}
