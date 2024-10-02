package com.assignment.bankingsystem.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionBatch {
    private final List<Transaction> transactions;
    private BigDecimal totalValue;
    private final int maxSize;
    private final BigDecimal maxValue;

    public TransactionBatch(int maxSize, BigDecimal maxValue) {
        this.transactions = new ArrayList<>();
        this.totalValue = BigDecimal.ZERO;
        this.maxSize = maxSize;
        this.maxValue = maxValue;
    }

    public boolean isFull() {
        return transactions.size() >= maxSize || totalValue.compareTo(maxValue) >= 0;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public int getSize() {
        return transactions.size();
    }

    public boolean canFitTransaction(Transaction transaction) {
        BigDecimal newTotal = totalValue.add(transaction.getAmount());
        return transactions.size() < maxSize && newTotal.compareTo(maxValue) <= 0;
    }

    public boolean addTransaction(Transaction transaction) {
        if (canFitTransaction(transaction)) {
            transactions.add(transaction);
            totalValue = totalValue.add(transaction.getAmount());
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "TransactionBatch{" +
                "countOfTransactions=" + transactions.size() +
                ", totalValueOfAllTransactions=" + totalValue +
                '}';
    }


}
