package com.example.payment.service;

import com.example.payment.model.Payment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final String FILE_NAME = "payments.txt";
    private final Path filePath = Paths.get(FILE_NAME);

    public PaymentService() {
        // Ensure the file exists on startup
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.err.println("Error initializing payments.txt: " + e.getMessage());
        }
    }

    /**
     * Reads all payment records from the local payments.txt file.
     * Handles exceptions gracefully and skips malformed rows.
     */
    public synchronized List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return payments;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Payment payment = Payment.fromCsv(line);
                if (payment != null) {
                    payments.add(payment);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading payments from file: " + e.getMessage());
        }
        return payments;
    }

    /**
     * Appends a new payment record to the file.
     * Generates a UUID for the paymentId if not already present.
     */
    public synchronized void savePayment(Payment payment) {
        if (payment.getPaymentId() == null || payment.getPaymentId().trim().isEmpty()) {
            payment.setPaymentId(UUID.randomUUID().toString());
        }
        
        // Ensure file exists
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.err.println("Error checking/creating file before save: " + e.getMessage());
            return;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            writer.write(payment.toCsv());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing payment to file: " + e.getMessage());
        }
    }

    /**
     * Updates the status of a specific payment by ID to "Success" or "Failed".
     * Rewrites the payments.txt file with the updated record.
     */
    public synchronized boolean updateStatus(String paymentId, String newStatus) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            return false;
        }
        if (!"Success".equalsIgnoreCase(newStatus) && !"Failed".equalsIgnoreCase(newStatus)) {
            return false;
        }

        List<Payment> payments = getAllPayments();
        boolean found = false;

        for (Payment payment : payments) {
            if (payment.getPaymentId().equals(paymentId)) {
                // Normalize status capitalization to match expectations
                payment.setStatus("Success".equalsIgnoreCase(newStatus) ? "Success" : "Failed");
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(payments);
        }

        return found;
    }

    /**
     * Deletes a payment by ID, but only if its status is "Failed".
     * Rewrites the payments.txt file with the remaining records.
     */
    public synchronized boolean deleteFailedPayment(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            return false;
        }

        List<Payment> payments = getAllPayments();
        Payment targetPayment = null;

        for (Payment payment : payments) {
            if (payment.getPaymentId().equals(paymentId)) {
                targetPayment = payment;
                break;
            }
        }

        // Only allow deletion if the status is "Failed"
        if (targetPayment != null && "Failed".equalsIgnoreCase(targetPayment.getStatus())) {
            payments.remove(targetPayment);
            rewriteFile(payments);
            return true;
        }

        return false;
    }

    /**
     * Helper method to overwrite payments.txt with the current list of payments.
     */
    private void rewriteFile(List<Payment> payments) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Payment p : payments) {
                writer.write(p.toCsv());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error rewriting payments file: " + e.getMessage());
        }
    }
}
