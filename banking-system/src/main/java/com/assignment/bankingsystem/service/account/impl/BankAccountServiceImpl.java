package com.assignment.bankingsystem.service.account.impl;

import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionType;
import com.assignment.bankingsystem.service.account.BankAccountService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class BankAccountServiceImpl implements BankAccountService {

    private final AtomicReference<BigDecimal> balance;

    public BankAccountServiceImpl() {
        this.balance = new AtomicReference<>(BigDecimal.ZERO);
    }

    @Override
    public void processTransaction(Transaction transaction) {
        BigDecimal amount = transaction.getAmount();
        if (transaction.getTransactionType() == TransactionType.CREDIT) {
            addTransactionAmountToBalance(amount);
        } else {
            deductTransactionAmountFromBalance(amount);
        }
    }

    @Override
    public double retrieveBalance() {
        return balance.get()
                .setScale(3, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private void addTransactionAmountToBalance(BigDecimal amount) {
        balance.updateAndGet(balance -> balance.add(amount));
    }

    private void deductTransactionAmountFromBalance(BigDecimal amount) {
        balance.updateAndGet(balance -> balance.subtract(amount));
    }
}
