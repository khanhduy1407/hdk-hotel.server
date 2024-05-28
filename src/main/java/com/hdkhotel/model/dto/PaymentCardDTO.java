package com.hdkhotel.model.dto;

import com.hdkhotel.validation.annotation.CardExpiry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardDTO {
  @NotBlank(message = "Tên chủ thẻ không được để trống")
  @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Tên chủ thẻ chỉ được chứa chữ cái và dấu cách")
  @Size(min = 3, max = 50, message = "Tên chủ thẻ phải dài từ 3 đến 50 ký tự")
  private String cardholderName;

  // 16 digits + Luhn check
  @CreditCardNumber(message = "Số thẻ không hợp lệ")
  private String cardNumber;

  @CardExpiry
  private String expirationDate;

  @Pattern(regexp = "^\\d{3}$", message = "CVC phải có 3 chữ số")
  private String cvc;
}
