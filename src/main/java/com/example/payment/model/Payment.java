package com.example.payment.model;

public class Payment {
    private String paymentId;
    private String userId;
    private String movieName;
    private Double amount;
    private String status; // "Success" or "Failed"
    private String date;   // "YYYY-MM-DD"
    private String purchaseType; // "Full Own" or "Rental"
    private Integer rentalDays;   // number of days (default 1)

    // Default constructor for form binding
    public Payment() {
        this.purchaseType = "Full Own";
        this.rentalDays = 1;
    }
}
