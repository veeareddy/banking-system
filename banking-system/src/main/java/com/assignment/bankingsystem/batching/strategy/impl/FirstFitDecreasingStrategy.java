package com.assignment.bankingsystem.batching.strategy.impl;

import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionBatch;
import com.assignment.bankingsystem.batching.strategy.BatchOptimizationStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class FirstFitDecreasingStrategy implements BatchOptimizationStrategy {


    @Override
    public List<TransactionBatch> optimizeBatches(List<Transaction> transactions, int maxBatchSize,
                                                  BigDecimal maxBatchValue) {
        List<TransactionBatch> batches = new ArrayList<>();

        // sort the transactions in descending order using prioriry queue (Max Heap)
        PriorityQueue<Transaction> maxHeap = new PriorityQueue<>((a, b) -> b.getAmount()
                .compareTo(a.getAmount()));

        maxHeap.addAll(transactions);

        while (!maxHeap.isEmpty()) {
            Transaction transaction = maxHeap.poll();
            boolean added = false;
            // Iterate over all the batches and see if you can fit the current transaction in any of the batches
            for (TransactionBatch batch : batches) {
                if (batch.canFitTransaction(transaction)) {
                    batch.addTransaction(transaction);
                    added = true;
                    break;
                }
            }
            // if the transaction doesnt fit in any of the batches , create a new batch and add to it
            if (!added) {
                TransactionBatch newBatch = new TransactionBatch(maxBatchSize, maxBatchValue);
                newBatch.addTransaction(transaction);
                batches.add(newBatch);
            }
        }

        return batches;
    }
}


