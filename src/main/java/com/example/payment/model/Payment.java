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

    // Parameterized constructor (legacy compatibility)
    public Payment(String paymentId, String userId, Double amount, String status, String date) {
        this(paymentId, userId, "Legacy Rental", amount, status, date, "Full Own", 1);
    }

    // Previous constructor (compatibility)
    public Payment(String paymentId, String userId, String movieName, Double amount, String status, String date) {
        this(paymentId, userId, movieName, amount, status, date, "Full Own", 1);
    }

    // Fully parameterized constructor
    public Payment(String paymentId, String userId, String movieName, Double amount, String status, String date, String purchaseType, Integer rentalDays) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.movieName = movieName;
        this.amount = amount;
        this.status = status;
        this.date = date;
        this.purchaseType = purchaseType;
        this.rentalDays = rentalDays;
    }

    // Getters and Setters
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMovieName() { return movieName; }
    public void setMovieName(String movieName) { this.movieName = movieName; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getPurchaseType() { return purchaseType; }
    public void setPurchaseType(String purchaseType) { this.purchaseType = purchaseType; }
    public Integer getRentalDays() { return rentalDays; }
    public void setRentalDays(Integer rentalDays) { this.rentalDays = rentalDays; }
}
