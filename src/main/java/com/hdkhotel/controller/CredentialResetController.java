package com.hdkhotel.controller;

/*@Controller
@RequiredArgsConstructor
public class CredentialResetController {
  private final UserService userService;

  @GetMapping("/reset-password")
  public String showResetPasswordForm(@ModelAttribute("resetPassword") ResetPasswordDTO resetPasswordDTO) {
    return "reset-password";
  }

  @PostMapping("/reset-password/save")
  public String resetUserPassword(@Valid @ModelAttribute("resetPassword") ResetPasswordDTO resetPasswordDTO, BindingResult result) {
    if (!resetPasswordDTO.getNewPassword().equals(resetPasswordDTO.getConfirmNewPassword())) {
      result.rejectValue("confirmNewPassword", "password.mismatch", "Mật khẩu mới không khớp!");
      return "reset-password";
    }

    try {
      userService.resetPassword(resetPasswordDTO);
    } catch (IllegalArgumentException e) {
      result.rejectValue("oldPassword", "password.incorrect", "Mật khẩu cũ không đúng!");
      return "reset-password";
    }

    return "redirect:/user-dashboard?resetSuccess";
  }
}*/
