package com.assignment.bankingsystem.service.audit.impl;

import com.assignment.bankingsystem.batching.strategy.BatchOptimizationStrategy;
import com.assignment.bankingsystem.model.Transaction;
import com.assignment.bankingsystem.model.TransactionBatch;
import com.assignment.bankingsystem.service.audit.AuditBatchingService;
import com.assignment.bankingsystem.service.audit.BatchSubmissionWorker;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
public class AuditBatchingServiceImpl implements AuditBatchingService {

    private final BlockingQueue<Transaction> auditQueue;
    private final BatchSubmissionWorker batchSubmissionWorker;
    private final BatchOptimizationStrategy batchStrategy;
    private final ExecutorService executorService;
    private final int maxBatchSize;
    private final BigDecimal maxBatchValue;
    private final int bufferSize;

    private volatile boolean running = false;

    @Autowired
    public AuditBatchingServiceImpl(BatchSubmissionWorker batchSubmissionWorker,
                                    BatchOptimizationStrategy batchStrategy,
                                    @Qualifier("auditExecutorService") ExecutorService executorService,
                                    @Qualifier("auditMaxBatchSize") int maxBatchSize,
                                    @Qualifier("auditMaxBatchValue") BigDecimal maxBatchValue,
                                    @Qualifier("auditBufferSize") int bufferSize) {
        this.auditQueue = new LinkedBlockingQueue<>();
        this.batchSubmissionWorker = batchSubmissionWorker;
        this.batchStrategy = batchStrategy;
        this.executorService = executorService;
        this.maxBatchSize = maxBatchSize;
        this.maxBatchValue = maxBatchValue;
        this.bufferSize = bufferSize;
    }


    @Override
    public void queueTransactionForAudit(Transaction transaction)  {
        try {
            auditQueue.put(transaction);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startAuditing() {
        running = true;
        executorService.submit(this::processAuditQueue);
    }

    private void processAuditQueue() {
        while (running && !Thread.currentThread()
                .isInterrupted()) {
            try {
                List<Transaction> buffer = new ArrayList<>();
                auditQueue.drainTo(buffer, bufferSize);

                if (buffer.isEmpty()) {
                    Thread.sleep(100);
                    continue;
                }

                List<TransactionBatch> optimizedBatches =
                        batchStrategy.optimizeBatches(buffer, maxBatchSize, maxBatchValue);

                submitBatchesToAuditSystem(optimizedBatches);
            } catch (InterruptedException e) {
                Thread.currentThread()
                        .interrupt();
                log.error("Audit processing interrupted", e);
            } catch (Exception e) {
                log.error("Error processing audit batch", e);
            }
        }
    }

    @Override
    @PreDestroy
    public void stopAuditing() {
        running = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void submitBatchesToAuditSystem(List<TransactionBatch> batches ) {
        printBatchInfo(batches);
        for (TransactionBatch batch : batches) {
            batchSubmissionWorker.submitAuditBatch(batch);
        }
    }

    private void printBatchInfo (List<TransactionBatch> batches) {

        log.info("           --------Submissions-------     ");
        for(TransactionBatch batch: batches) {
            log.info(batch.toString());
        }
        log.info("------------------------------------- ");

    }

}
