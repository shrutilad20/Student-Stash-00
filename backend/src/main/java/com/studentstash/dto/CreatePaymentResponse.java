package com.studentstash.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CreatePaymentResponse {

    private String razorpayOrderId;
    private String razorpayKeyId; // public key, safe to expose to frontend
    private BigDecimal amount;
    private String currency;

}