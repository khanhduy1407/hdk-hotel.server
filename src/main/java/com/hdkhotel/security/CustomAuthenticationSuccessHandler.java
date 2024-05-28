package com.hdkhotel.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
    log.warn("Đang chuyển hướng người dùng được xác thực đến trang đích được chỉ định theo vai trò");

    String redirectUrl = RedirectUtil.getRedirectUrl(authentication);

    log.info("Đường dẫn chuyển hướng: " + redirectUrl);

    if (redirectUrl == null) {
      throw new IllegalStateException("Không thể xác định vai trò của người dùng để chuyển hướng");
    }

    new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
  }
}
