package com.assignment.bankingsystem.service.transcation.impl;

import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.service.account.BankAccountService;
import com.assignment.bankingsystem.service.audit.AuditBatchingService;

import com.assignment.bankingsystem.service.transcation.TransactionProcessingService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class TransactionProcessingServiceImpl implements TransactionProcessingService {

    private final BlockingQueue<Transaction> transactionQueue;
    private final BankAccountService bankAccountService;
    private final AuditBatchingService auditBatchingService;
    private final ExecutorService executorService;

    private volatile boolean running = false;

    @Autowired
    public TransactionProcessingServiceImpl(
            @Qualifier("transactionQueue") BlockingQueue<Transaction> transactionQueue,
            BankAccountService bankAccountService,
            AuditBatchingService auditBatchingService,
            @Qualifier("transactionProcessingExecutor") ExecutorService executorService) {
        this.transactionQueue = transactionQueue;
        this.bankAccountService = bankAccountService;
        this.auditBatchingService = auditBatchingService;
        this.executorService = executorService;
    }

    @Override
    public void startProcessing() {
        running = true;
        executorService.submit(this::processTransactions);
        log.info("Transaction processing started");
    }

    private void processTransactions() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                Transaction transaction = transactionQueue.take();
                processTransaction(transaction);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Transaction processing interrupted", e);
            } catch (Exception e) {
                log.error("Error processing transaction", e);
            }
        }
    }

    private void processTransaction(Transaction transaction) {
        try {
            bankAccountService.processTransaction(transaction);
            auditBatchingService.queueTransactionForAudit(transaction);
            //log.debug("Processed transaction: {}", transaction);
        } catch (Exception e) {
            log.error("Error processing transaction: {}", transaction, e);
            // Implement error handling logic here
        }
    }

    @Override
    @PreDestroy
    public void stopProcessing() {
        running = false;
        // Note: We don't shut down the executorService here as it's managed by Spring
        log.info("Transaction processing stopped");
    }
}