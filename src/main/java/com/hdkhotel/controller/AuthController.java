package com.hdkhotel.controller;

import com.hdkhotel.exception.UsernameAlreadyExistsException;
import com.hdkhotel.model.enums.RoleType;
import com.hdkhotel.model.dto.UserRegistrationDTO;
import com.hdkhotel.security.RedirectUtil;
import com.hdkhotel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {
  private final UserService userService;

  @GetMapping("/")
  public String homePage(Authentication authentication) {
    String redirect = getAuthenticatedUserRedirectUrl(authentication);
    if (redirect != null) return redirect;
    log.debug("Truy cập trang chủ");
    return "index";
  }

  @GetMapping("/login")
  public String loginPage(Authentication authentication) {
    String redirect = getAuthenticatedUserRedirectUrl(authentication);
    if (redirect != null) return redirect;
    log.debug("Truy cập trang đăng nhập");
    return "login";
  }

  @GetMapping("/register/customer")
  public String showCustomerRegistrationForm(@ModelAttribute("user") UserRegistrationDTO registrationDTO, Authentication authentication) {
    String redirect = getAuthenticatedUserRedirectUrl(authentication);
    if (redirect != null) return redirect;
    log.info("Hiển thị form đăng ký của khách hàng");
    return "register-customer";
  }

  @PostMapping("/register/customer")
  public String registerCustomerAccount(@Valid @ModelAttribute("user") UserRegistrationDTO registrationDTO, BindingResult result) {
    log.info("Đang cố gắng đăng ký tài khoản khách hàng: {}", registrationDTO.getUsername());
    registrationDTO.setRoleType(RoleType.CUSTOMER);
    return registerUser(registrationDTO, result, "register-customer", "register/customer");
  }

  @GetMapping("/register/manager")
  public String showManagerRegistrationForm(@ModelAttribute("user") UserRegistrationDTO registrationDTO, Authentication authentication) {
    String redirect = getAuthenticatedUserRedirectUrl(authentication);
    if (redirect != null) return redirect;
    log.info("Hiển thị form đăng ký người quản lý");
    return "register-manager";
  }

  @PostMapping("/register/manager")
  public String registerManagerAccount(@Valid @ModelAttribute("user") UserRegistrationDTO registrationDTO, BindingResult result) {
    log.info("Đang cố gắng đăng ký tài khoản người quản lý: {}", registrationDTO.getUsername());
    registrationDTO.setRoleType(RoleType.HOTEL_MANAGER);
    return registerUser(registrationDTO, result, "register-manager", "register/manager");
  }

  private String registerUser(UserRegistrationDTO registrationDTO, BindingResult result, String view, String redirectUrl) {
    if (result.hasErrors()) {
      log.warn("Đăng ký không thành công do lỗi xác thực: {}", result.getAllErrors());
      return view;
    }
    try {
      userService.saveUser(registrationDTO);
      log.info("Đăng ký người dùng thành công: {}", registrationDTO.getUsername());
    } catch (UsernameAlreadyExistsException e) {
      log.error("Đăng ký không thành công do tên người dùng đã tồn tại: {}", e.getMessage());
      result.rejectValue("username", "user.exists", e.getMessage());
      return view;
    }
    return "redirect:/" + redirectUrl + "?success";
  }

  private String getAuthenticatedUserRedirectUrl(Authentication authentication) {
    if (authentication != null && authentication.isAuthenticated()) {
      String redirectUrl = RedirectUtil.getRedirectUrl(authentication);
      if (redirectUrl != null) {
        return "redirect:" + redirectUrl;
      }
    }
    return null;
  }
}
