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

    /**
     * UI 1: Dashboard Overview
     * Renders stats, health meter, and the last 5 recent transactions.
     */
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

        // Extract last 5 activities, reversed so the newest are on top
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

    /**
     * UI 2: Add Payment Form Page
     * Renders a blank payment log form with default values.
     */
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

    /**
     * UI 3: Transaction Ledger Log Page
     * Renders a full registry of all transaction lines in the file.
     */
    @GetMapping("/payments/list")
    public String viewLedgerList(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        return "payment-list";
    }

    /**
     * Submit Handler: Save manual payment log.
     * Redirects to the transaction list on completion.
     */
    @PostMapping("/payments/create")
    public String createPayment(@ModelAttribute("newPayment") Payment payment, 
                                RedirectAttributes redirectAttributes) {
        // Backend Validation
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

        // Set defaults automatically
        payment.setStatus("Success");
        payment.setDate(LocalDate.now().toString());

        // Calculate amount based on purchase option
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

    /**
     * UI 4: Printable Receipt Page
     * Renders a cinema ticket receipt with a QR code.
     */
    @GetMapping("/payments/receipt/{id}")
    public String viewReceipt(@PathVariable("id") String paymentId, Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        Payment target = payments.stream()
                .filter(p -> p.getPaymentId().equals(paymentId))
                .findFirst()
                .orElse(null);

        if (target == null) {
            model.addAttribute("errorMessage", "Transaction record not found.");
            return "redirect:/payments/list";
        }

        model.addAttribute("payment", target);
        return "receipt";
    }

    /**
     * Toggle Status Handler: Change payment status.
     * Redirects back to the transaction ledger view.
     */
    @PostMapping("/payments/toggle/{id}")
    public String togglePaymentStatus(@PathVariable("id") String paymentId, 
                                      RedirectAttributes redirectAttributes) {
        List<Payment> payments = paymentService.getAllPayments();
        Payment target = payments.stream()
                .filter(p -> p.getPaymentId().equals(paymentId))
                .findFirst()
                .orElse(null);

        if (target == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Transaction record not found.");
            return "redirect:/payments/list";
        }

        String newStatus = "Success".equalsIgnoreCase(target.getStatus()) ? "Failed" : "Success";
        boolean updated = paymentService.updateStatus(paymentId, newStatus);
        
        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "Status for transaction " + paymentId + " updated to " + newStatus + ".");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update transaction status.");
        }
        
        return "redirect:/payments/list";
    }

    /**
     * Delete failed transactions handler.
     * Redirects back to the transaction ledger view.
     */
    @PostMapping("/payments/delete/{id}")
    public String deletePayment(@PathVariable("id") String paymentId, 
                                RedirectAttributes redirectAttributes) {
        boolean deleted = paymentService.deleteFailedPayment(paymentId);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Failed transaction " + paymentId + " was permanently removed.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to remove transaction. It must have a status of 'Failed'.");
        }
        return "redirect:/payments/list";
    }
}
