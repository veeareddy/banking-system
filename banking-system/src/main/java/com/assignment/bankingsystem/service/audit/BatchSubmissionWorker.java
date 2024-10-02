package com.assignment.bankingsystem.service.audit;

import com.assignment.bankingsystem.model.TransactionBatch;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;

@Component
@Slf4j
public class BatchSubmissionWorker {

    private final ExecutorService batchSubmissionExecutor;

    public BatchSubmissionWorker(@Qualifier("batchSubmissionExecutor") ExecutorService batchSubmissionExecutor) {
        this.batchSubmissionExecutor = batchSubmissionExecutor;
    }

    public void submitAuditBatch(TransactionBatch batch) {
        batchSubmissionExecutor.submit(() -> {
            // in realworld this could be external call to audit system
            log.info("Submitting audit batch: {} transactions, total value: {}",
                        batch.getSize(), batch.getTotalValue());
            try {
                Thread.sleep(1000); // Simulating a 1-second processing time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Batch submission interrupted", e);
            }
        });

    }
}
