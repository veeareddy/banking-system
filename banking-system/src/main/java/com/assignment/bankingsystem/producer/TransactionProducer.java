package com.assignment.bankingsystem.producer;

import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionType;
import com.assignment.bankingsystem.util.TransactionUtil;

import java.util.concurrent.BlockingQueue;

public class TransactionProducer implements  Runnable{

    private BlockingQueue<Transaction> queue;
    private TransactionType transactionType;

    public TransactionProducer(BlockingQueue<Transaction> queue, TransactionType transactionType) {
        this.queue = queue;
        this.transactionType = transactionType;
    }

    @Override
    public void run() {
        Transaction transaction = TransactionUtil.generateRandomTransaction(transactionType);
        //System.out.println(transaction.toString());
        try {
            queue.put(transaction);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
