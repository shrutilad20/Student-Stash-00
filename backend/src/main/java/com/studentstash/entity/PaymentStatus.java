package com.studentstash.entity;

public enum PaymentStatus {
    CREATED,   // Razorpay order created, awaiting user action
    SUCCESS,   // signature verified, order placed
    FAILED     // verification failed or payment was rejected
}