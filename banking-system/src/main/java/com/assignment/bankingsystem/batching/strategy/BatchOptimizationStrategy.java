package com.assignment.bankingsystem.batching.strategy;

import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionBatch;

import java.math.BigDecimal;
import java.util.List;

public interface BatchOptimizationStrategy {

    /**
     *
     * @param transactions  List of transactions be to be optimized in batches
     * @param maxBatchSize
     * @param maxBatchValue
     * @return
     */
    List<TransactionBatch> optimizeBatches(List<Transaction> transactions, int maxBatchSize, BigDecimal maxBatchValue);

}
