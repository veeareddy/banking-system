package com.assignment.bankingsystem.util;

import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionType;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

public class TransactionUtil {

    private static final int MIN_AMOUNT = 200;
    private static final int MAX_AMOUNT = 500000;

    public static Transaction generateRandomTransaction() {
        double amount = getRandomAmount();
        TransactionType transactionType = getRandomTransactionType();
        return new Transaction(new BigDecimal(amount),transactionType);
    }

    private static double getRandomAmount() {
        double amount = ThreadLocalRandom.current().nextDouble(MIN_AMOUNT, MAX_AMOUNT+1);
        return amount;
    }

    private static TransactionType getRandomTransactionType() {
        boolean isCredit = ThreadLocalRandom.current().nextBoolean();
        return isCredit ? TransactionType.CREDIT : TransactionType.DEBIT;
    }

    public static Transaction generateRandomTransaction(TransactionType transactionType) {
        double amount = getRandomAmount();
        return new Transaction(new BigDecimal(amount),transactionType);
    }

}
