package com.studentstash.service;

import com.razorpay.RazorpayClient;
import com.studentstash.dto.CreatePaymentResponse;
import com.studentstash.dto.OrderResponse;
import com.studentstash.entity.Payment;
import com.studentstash.entity.PaymentStatus;
import com.studentstash.entity.User;
import com.studentstash.exception.BadRequestException;
import com.studentstash.exception.ResourceNotFoundException;
import com.studentstash.repository.PaymentRepository;
import com.studentstash.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderService orderService;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    @Transactional
    public CreatePaymentResponse createPayment(String shippingAddress, String buyerEmail) throws Exception {
        User buyer = userRepository.findByEmail(buyerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var cart = cartService.getCart(buyerEmail);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot proceed to payment with an empty cart");
        }

        BigDecimal amount = cart.getGrandTotal();

        // Razorpay expects amount in the smallest currency unit (paise for INR)
        long amountInPaise = amount.multiply(BigDecimal.valueOf(100)).longValue();

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "receipt_" + System.currentTimeMillis());

        com.razorpay.Order razorpayOrder = razorpayClient.orders.create(orderRequest);

        Payment payment = new Payment();
        payment.setRazorpayOrderId(razorpayOrder.get("id"));
        payment.setBuyer(buyer);
        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.CREATED);
        payment.setShippingAddress(shippingAddress);

        paymentRepository.save(payment);

        return new CreatePaymentResponse(
                payment.getRazorpayOrderId(),
                razorpayKeyId,
                amount,
                "INR"
        );
    }

    @Transactional
    public OrderResponse verifyPayment(String razorpayOrderId,
                                        String razorpayPaymentId,
                                        String razorpaySignature,
                                        String buyerEmail) {

        Payment payment = paymentRepository.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));

        if (!payment.getBuyer().getEmail().equals(buyerEmail)) {
            throw new BadRequestException("This payment does not belong to you");
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            throw new BadRequestException("This payment has already been processed");
        }

        boolean isValidSignature = verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

        if (!isValidSignature) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new BadRequestException("Payment verification failed. Signature mismatch.");
        }

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
        payment.setStatus(PaymentStatus.SUCCESS);

        // Signature is valid — now safe to actually place the order
        OrderResponse orderResponse = orderService.placeOrder(buyerEmail, payment.getShippingAddress());

        paymentRepository.save(payment);

        return orderResponse;
    }

    private boolean verifySignature(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    razorpayKeySecret.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hash = mac.doFinal(payload.getBytes());
            String generatedSignature = HexFormat.of().formatHex(hash);

            return generatedSignature.equals(razorpaySignature);
        } catch (Exception e) {
            return false;
        }
    }

}