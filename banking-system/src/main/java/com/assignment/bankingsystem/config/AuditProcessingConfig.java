package com.assignment.bankingsystem.config;

import com.assignment.bankingsystem.batching.strategy.BatchOptimizationStrategy;
import com.assignment.bankingsystem.batching.strategy.impl.FirstFitDecreasingStrategy;
import com.assignment.bankingsystem.service.audit.AuditBatchingService;
import com.assignment.bankingsystem.service.audit.BatchSubmissionWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AuditProcessingConfig {

    @Value("${audit.batch.max-size:1000}")
    private int maxBatchSize;

    @Value("${audit.batch.max-value:1000000}")
    private BigDecimal maxBatchValue;

    @Value("${audit.processor.buffer-size:2000}")
    private int bufferSize;

    @Value("${audit.thread-pool-size:1}")
    private int auditThreadPoolSize;

    @Value("${audit.submission.thread-pool-size:5}")
    private int batchSubmissionThreadPoolSize;


    @Bean
    public BatchOptimizationStrategy batchOptimizationStrategy() {
        return new FirstFitDecreasingStrategy();
    }

    @Bean(name = "auditExecutorService")
    public ExecutorService auditExecutorService() {
        return Executors.newFixedThreadPool(auditThreadPoolSize);
    }

    @Bean(name = "batchSubmissionExecutor")
    public ExecutorService batchSubmissionExecutor() {
        return Executors.newFixedThreadPool(batchSubmissionThreadPoolSize);
    }

    @Bean
    public int auditMaxBatchSize() {
        return maxBatchSize;
    }

    @Bean
    public BigDecimal auditMaxBatchValue() {
        return maxBatchValue;
    }

    @Bean
    public int auditBufferSize() {
        return bufferSize;
    }
}


