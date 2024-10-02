package com.assignment.bankingsystem.controller;

import com.assignment.bankingsystem.*;
import com.assignment.bankingsystem.model.BalanceResponse;
import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionType;
import com.assignment.bankingsystem.producer.TransactionProducer;
import com.assignment.bankingsystem.service.account.BankAccountService;
import com.assignment.bankingsystem.service.account.impl.BankAccountServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/v1/account")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/balance")
    public BalanceResponse getBalance() {
        double balance = bankAccountService.retrieveBalance();
        return new BalanceResponse(balance,"POUND");
    }

}
