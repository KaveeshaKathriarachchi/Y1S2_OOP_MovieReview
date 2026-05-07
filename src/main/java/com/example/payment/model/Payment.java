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
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPurchaseType() {
        return purchaseType;
    }

    public void setPurchaseType(String purchaseType) {
        this.purchaseType = purchaseType;
    }

    public Integer getRentalDays() {
        return rentalDays;
    }

    public void setRentalDays(Integer rentalDays) {
        this.rentalDays = rentalDays;
    }

    // Helper method to convert Payment to CSV format
    public String toCsv() {
        return String.join(",", 
            escapeCsv(paymentId), 
            escapeCsv(userId), 
            String.valueOf(amount), 
            escapeCsv(status), 
            escapeCsv(date),
            escapeCsv(movieName),
            escapeCsv(purchaseType),
            String.valueOf(rentalDays)
        );
    }

    // Helper method to parse CSV line into a Payment object
    public static Payment fromCsv(String csvLine) {
        if (csvLine == null || csvLine.trim().isEmpty()) {
            return null;
        }
        
        // Simple comma split. If fields have commas, they can be escaped, 
        // but for this standard project simple comma separation is sufficient.
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 5) {
            return null;
        }

        try {
            String paymentId = unescapeCsv(parts[0]);
            String userId = unescapeCsv(parts[1]);
            Double amount = Double.parseDouble(parts[2]);
            String status = unescapeCsv(parts[3]);
            String date = unescapeCsv(parts[4]);
            String movieName = parts.length > 5 ? unescapeCsv(parts[5]) : "Legacy Rental";
            String purchaseType = parts.length > 6 ? unescapeCsv(parts[6]) : "Full Own";
            Integer rentalDays = parts.length > 7 ? Integer.parseInt(parts[7].trim()) : 1;
            
            return new Payment(paymentId, userId, movieName, amount, status, date, purchaseType, rentalDays);
        } catch (NumberFormatException e) {
            // Handle parsing errors gracefully
            return null;
        }
    }

    private static String escapeCsv(String val) {
        if (val == null) return "";
        // If there's a comma, we can wrap or replace. 
        // For simplicity, we just replace commas with a space or leave as is.
        return val.replace(",", " ");
    }

    private static String unescapeCsv(String val) {
        return val == null ? "" : val.trim();
    }
}
