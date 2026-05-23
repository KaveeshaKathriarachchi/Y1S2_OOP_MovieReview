package com.movieplatform.api.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserService userService;

    public PaymentService(PaymentRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    public Payment process(PaymentRequest request) {
        Payment payment = new Payment();
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setCurrency(request.getCurrency());
        payment.setTransactionId(request.getTransactionId());
        payment.setStatus(request.isSuccess() ? "SUCCESS" : "FAILED");
        payment.setPaidAt(LocalDateTime.now());

        userService.updatePaidStatus(request.getUserId(), request.isSuccess());
        return paymentRepository.save(payment);
    }

    public List<Payment> allPayments() {
        return paymentRepository.findAll();
    }

    public List<Payment> paymentsByUser(String userId) {
        return paymentRepository.findByUserId(userId);
    }
}
