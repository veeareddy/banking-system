package com.assignment.bankingsystem.service.transcation.impl;

import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionType;
import com.assignment.bankingsystem.producer.TransactionProducer;
import com.assignment.bankingsystem.service.transcation.TransactionProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
public class TransactionProducerServiceImpl implements TransactionProducerService {
    private final BlockingQueue<Transaction> transactionQueue;
    private final ScheduledExecutorService scheduledExecutorService;
    private final long delayInMillis;
    private List<ScheduledFuture<?>> scheduledTasks;

    @Autowired
    public TransactionProducerServiceImpl(BlockingQueue<Transaction> transactionQueue,
                                          @Qualifier("transactionProducerScheduler") ScheduledExecutorService scheduledExecutorService,
                                          @Qualifier("transactionDelayInMillis") long delayInMillis) {
        this.transactionQueue = transactionQueue;
        this.scheduledExecutorService = scheduledExecutorService;
        this.delayInMillis = delayInMillis;
        this.scheduledTasks = new ArrayList<>();
    }

    @Override
    public void startProducing() {
        scheduledTasks.add(scheduledExecutorService.scheduleAtFixedRate(
                new TransactionProducer(transactionQueue, TransactionType.CREDIT),
                0, delayInMillis, TimeUnit.MILLISECONDS
        ));
        scheduledTasks.add(scheduledExecutorService.scheduleAtFixedRate(
                new TransactionProducer(transactionQueue, TransactionType.DEBIT),
                0, delayInMillis, TimeUnit.MILLISECONDS
        ));
    }

    @Override
    public void stopProducing() {
        scheduledTasks.forEach(task -> task.cancel(true));
        scheduledTasks.clear();
    }
}

