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
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            System.err.println("Error initializing payments.txt: " + e.getMessage());
        }
    }

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

    public synchronized void savePayment(Payment payment) {
        if (payment.getPaymentId() == null || payment.getPaymentId().trim().isEmpty()) {
            payment.setPaymentId(UUID.randomUUID().toString());
        }
        
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
}
