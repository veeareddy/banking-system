package com.assignment.bankingsystem.config;

import com.assignment.bankingsystem.model.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class TransactionConfig {

    @Value("${transaction.producer.ratePerSecond:25}")
    private int ratePerSecond;

    @Value("${transaction.producer.threadPoolSize:2}")
    private int producerThreadPoolSize;

    @Value("${transaction.processor.threadPoolSize:2}")
    private int processorThreadPoolSize;

    @Value("${transaction.amount.max:500000}")
    private int maxAmount;

    @Value("${transaction.amount.min:20}")
    private int minAmount;

    @Bean
    public BlockingQueue<Transaction> transactionQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean(name = "transactionProducerScheduler")
    public ScheduledExecutorService scheduledExecutorService() {
        return Executors.newScheduledThreadPool(producerThreadPoolSize);
    }

    @Bean(name = "transactionProcessingExecutor")
    public ExecutorService auditExecutorService() {
        return Executors.newFixedThreadPool(processorThreadPoolSize);
    }


    @Bean
    public int transactionRatePerSecond() {
        return ratePerSecond;
    }

    @Bean
    // converts rate to delay in milli seconds ex: 25 per second means delay between two transactions is 40
    public long transactionDelayInMillis() {
        return 1000 / ratePerSecond;
    }

    @Bean
    public int maxTransactionAmount() {
        return maxAmount;
    }

    @Bean
    public int minTransactionAmount() {
        return minAmount;
    }

}
