package com.assignment.bankingsystem.intializer;


import com.assignment.bankingsystem.service.audit.AuditBatchingService;
import com.assignment.bankingsystem.service.transcation.TransactionProcessingService;
import com.assignment.bankingsystem.service.transcation.TransactionProducerService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemInitializer {

    private final TransactionProducerService transactionProducerService;
    private final TransactionProcessingService transactionProcessingService;
    private final AuditBatchingService auditBatchingService;

    @Autowired
    public SystemInitializer(TransactionProducerService transactionProducerService,
                             TransactionProcessingService transactionProcessingService,
                             AuditBatchingService auditBatchingService) {
        this.transactionProducerService = transactionProducerService;
        this.transactionProcessingService = transactionProcessingService;
        this.auditBatchingService = auditBatchingService;
    }

    @PostConstruct
    public void initialize() {
        transactionProducerService.startProducing();
        transactionProcessingService.startProcessing();
        auditBatchingService.startAuditing();
        System.out.println("System has been started . Transaction production ,processing, auditBatching is in progress ");
    }
}