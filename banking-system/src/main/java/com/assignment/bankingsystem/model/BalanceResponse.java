package com.assignment.bankingsystem.model;

import lombok.Data;

@Data
public class BalanceResponse {

    private double balance;
    private String currency;

    public BalanceResponse(double balance, String currency) {
        this.balance = balance;
        this.currency = currency;
    }
}
