package com.assignment.bankingsystem.batching.strategy.impl;

import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionBatch;
import com.assignment.bankingsystem.batching.strategy.BatchOptimizationStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class GreedyDecreasingBatchStrategy implements BatchOptimizationStrategy {

    @Override
    public List<TransactionBatch> optimizeBatches(List<Transaction> transactions, int maxBatchSize,
                                                  BigDecimal maxBatchValue) {

        List<TransactionBatch> batches = new ArrayList<>();
        // sort them in descending order of transaction value
        PriorityQueue<Transaction> maxHeap = new PriorityQueue<>(
                (a, b) -> b.getAmount()
                        .compareTo(a.getAmount())
        );

        maxHeap.addAll(transactions);

        TransactionBatch currentBatch = new TransactionBatch(maxBatchSize, maxBatchValue);

        while (!maxHeap.isEmpty()) {
            Transaction transaction = maxHeap.poll();
            // check if the current batch can take this transaction
            if (currentBatch.canFitTransaction(transaction)) {
                currentBatch.addTransaction(transaction);
            } else {
                // add current batch to the list of batchs and put current transaction in new batch
                batches.add(currentBatch);
                currentBatch = new TransactionBatch(maxBatchSize, maxBatchValue);
                currentBatch.addTransaction(transaction);
            }
        }
        // add last batch
        if (!currentBatch.getTransactions()
                .isEmpty()) {
            batches.add(currentBatch);
        }

        return batches;
    }
}
