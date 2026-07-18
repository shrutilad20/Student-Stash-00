package com.studentstash.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

}