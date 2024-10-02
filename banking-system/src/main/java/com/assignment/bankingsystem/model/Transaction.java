package com.assignment.bankingsystem.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {

    private String id;
    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDateTime transactionTime;

    public Transaction(BigDecimal amount, TransactionType transactionType) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionTime = LocalDateTime.now();
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", transactionType=" + transactionType +
                ", transactionTime=" + transactionTime +
                '}';
    }
}
