package com.assignment.bankingsystem.service.account;

import com.assignment.bankingsystem.model.Transaction;
import org.springframework.stereotype.Service;

/**
 * Service to aggregate transactions tracking the overall balance for an account.
 */
@Service
public interface BankAccountService {
    /**
     * Process a given transaction - this is to be called by the credit and debit generation threads.
     *
     * @param transaction transaction to process
     */
    void processTransaction(Transaction transaction);
    /**
     * Retrieve the balance in the account
     */
    double retrieveBalance();
}
