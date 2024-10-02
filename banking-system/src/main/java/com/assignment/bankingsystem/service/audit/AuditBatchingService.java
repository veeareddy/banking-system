package com.assignment.bankingsystem.service.audit;

import com.assignment.bankingsystem.model.Transaction;

public interface AuditBatchingService {
    void queueTransactionForAudit(Transaction transaction);
    void startAuditing();
    void stopAuditing();
}
