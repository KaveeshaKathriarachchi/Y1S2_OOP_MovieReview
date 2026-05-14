package com.example.payment.controller;

import com.example.payment.model.Payment;
import com.example.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping({"/", "/payments/dashboard"})
    public String viewDashboard(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        
        long totalCount = payments.size();
        long successCount = payments.stream()
                .filter(p -> "Success".equalsIgnoreCase(p.getStatus()))
                .count();
        long failedCount = totalCount - successCount;
        double totalAmount = payments.stream()
                .filter(p -> "Success".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();

        List<Payment> recentPayments = new ArrayList<>();
        int startIdx = payments.size() - 1;
        int endIdx = Math.max(0, payments.size() - 5);
        for (int i = startIdx; i >= endIdx; i--) {
            recentPayments.add(payments.get(i));
        }

        model.addAttribute("totalCount", totalCount);
        model.addAttribute("successCount", successCount);
        model.addAttribute("failedCount", failedCount);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("recentPayments", recentPayments);
        
        return "dashboard";
    }

    @GetMapping("/payments/new")
    public String viewAddPaymentForm(Model model) {
        if (!model.containsAttribute("newPayment")) {
            Payment defaultPayment = new Payment();
            defaultPayment.setDate(LocalDate.now().toString());
            defaultPayment.setStatus("Success");
            model.addAttribute("newPayment", defaultPayment);
        }
        return "add-payment";
    }

    @PostMapping("/payments/create")
    public String createPayment(@ModelAttribute("newPayment") Payment payment, 
                                RedirectAttributes redirectAttributes) {
        if (payment.getUserId() == null || payment.getUserId().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "User ID is required.");
            redirectAttributes.addFlashAttribute("newPayment", payment);
            return "redirect:/payments/new";
        }
        if (payment.getMovieName() == null || payment.getMovieName().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Movie Name is required.");
            redirectAttributes.addFlashAttribute("newPayment", payment);
            return "redirect:/payments/new";
        }

        payment.setStatus("Success");
        payment.setDate(LocalDate.now().toString());

        if ("Rental".equalsIgnoreCase(payment.getPurchaseType())) {
            if (payment.getRentalDays() == null || payment.getRentalDays() < 1) {
                payment.setRentalDays(1);
            }
            payment.setAmount(1.99 * payment.getRentalDays());
        } else {
            payment.setPurchaseType("Full Own");
            payment.setRentalDays(1);
            payment.setAmount(9.99);
        }

        paymentService.savePayment(payment);
        redirectAttributes.addFlashAttribute("successMessage", "Payment logged successfully.");
        return "redirect:/payments/receipt/" + payment.getPaymentId();
    }
}
