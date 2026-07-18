package com.studentstash.controller;

import com.studentstash.dto.CreatePaymentRequest;
import com.studentstash.dto.CreatePaymentResponse;
import com.studentstash.dto.OrderResponse;
import com.studentstash.dto.VerifyPaymentRequest;
import com.studentstash.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('BUYER')")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication) throws Exception {
        CreatePaymentResponse response = paymentService.createPayment(
                request.getShippingAddress(), authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify")
    public ResponseEntity<OrderResponse> verifyPayment(
            @Valid @RequestBody VerifyPaymentRequest request,
            Authentication authentication) {
        OrderResponse response = paymentService.verifyPayment(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature(),
                authentication.getName());
        return ResponseEntity.ok(response);
    }

}