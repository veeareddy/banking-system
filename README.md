# Banking System Assignment Solution

## Problem Overview
Implement a system that:
- Tracks a bank account balance
- Supports credits, debits, and balance inquiries
- Sends batches of transactions to a downstream audit system
- Optimizes batches to minimize costs (max 1000 transactions or £1,000,000 per batch)


## Solution Architecture

### Class Structure

1. **BankingSystemApplication**
   - Main class that initializes and starts all components
   

2. **TransactionProducer**
   - Generates random credit and debit transactions
   - Implements Runnable for concurrent operation

3. **Transaction**
   - Model class representing a single transaction
   - Contains amount, type (CREDIT/DEBIT)

4. **BankAccountService** (interface)
   - Defines methods for processing transactions and retrieving balance

5. **BankAccountServiceImpl**
   - Implements BankAccountService
   - Manages account balance using AtomicReference for thread safety

6. **TransactionProcessor**
   - Consumes transactions from a queue
   - Processes transactions using BankAccountService
   - Sends processed transactions to AuditSystem

7. **AuditBatchingService**
   - Receives transactions for auditing
   - Implements batch optimization algorithm
   - Submits optimized batches to a simulated downstream system

8. **BatchOptimizationStrategy** (interface)
   - Defines method for optimizing transaction batches

9. **FirstFitDecreasingStrategy**
   - Implements BatchOptimizationStrategy
   - Uses First-Fit Decreasing algorithm for batch optimization

10. **TransactionBatch**
    - Represents a batch of transactions for auditing
    - Tracks batch size and total value
11. **SystemIntializer**
    - Intializes the system

### Component Interactions

1. TransactionProducer -> BlockingQueue<Transaction>
   - Produces transactions and adds them to the shared queue

2. TransactionProcessor -> BlockingQueue<Transaction>
   - Consumes transactions from the shared queue

3. TransactionProcessor -> BankAccountService
   - Processes each transaction, updating the account balance

4. TransactionProcessor -> AuditBatchingService
   - Sends processed transactions for auditing

5. AuditBatchingService -> BatchOptimizationStrategy
   - Uses the strategy to optimize batches of transactions

6. AuditBatchingService -> (Simulated) Downstream Audit System
   - Submits optimized batches for auditing

## Key Algorithms

### First-Fit Decreasing (FFD) for Batch Optimization

```java
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
    
```

This algorithm:
1. Sorts transactions by value (largest first) using a max heap
2. Attempts to add each transaction to existing batches
3. Creates a new batch if the transaction doesn't fit in any existing batch
4. Ensures each batch respects both size (1000) and value (£1,000,000) limits

## Concurrency and Thread Safety

- Use of `BlockingQueue` for thread-safe producer-consumer pattern
- `AtomicReference<BigDecimal>` for thread-safe balance updates
- Separate threads for transaction production, processing, and audit batch submission

## Scalability Considerations

- The system can handle increased transaction volumes by adjusting the number of producer and processor threads
- Batch optimization algorithm efficiently handles large numbers of transactions
- Asynchronous audit batch submission prevents bottlenecks in transaction processing

## Design Patterns and Flexibility

### Strategy Pattern for Batch Optimization

Our solution implements the Strategy Pattern for batch optimization, allowing for flexible and interchangeable optimization algorithms. This design decision provides several benefits:

1. **Modularity**: The batch optimization logic is encapsulated in separate strategy classes.
2. **Extensibility**: New optimization strategies can be easily added without modifying existing code.
3. **Runtime Flexibility**: The optimization strategy can be changed at runtime if needed.

#### Implementation

1. **BatchOptimizationStrategy Interface**
   ```java
   public interface BatchOptimizationStrategy {
       List<TransactionBatch> optimizeBatches(List<Transaction> transactions, int maxBatchSize, BigDecimal maxBatchValue);
   }
   ```

2. **Concrete Strategy Classes**
   - `FirstFitDecreasingStrategy`: Implements the First-Fit Decreasing algorithm (as shown earlier).
   - `GreedyStrategy`: An alternative implementation using a simpler greedy approach.

3. **Usage in AuditBatchingServiceImpl**

   ```java
    @Bean
    public BatchOptimizationStrategy batchOptimizationStrategy() {
        return new FirstFitDecreasingStrategy();
    }

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
   ```

### Alternative Optimization Strategies

1. **First-Fit Decreasing (FFD) Strategy**
   - Already implemented as shown earlier.
   - Optimal for minimizing the number of batches.

2. **Greedy Strategy**
   ```java
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
   ```
   - Simpler implementation, potentially faster for very large transaction volumes.
   - May produce more batches than FFD in some cases.

### Comparing Strategies

1. **First-Fit Decreasing (FFD)**
   - Pros: Optimizes for minimum number of batches, efficient for most scenarios.
   - Cons: Requires sorting, which can be costly for very large datasets.

2. **Greedy**
   - Pros: Simpler implementation, potentially faster for extremely large datasets.
   - Cons: May not always produce the optimal number of batches.

### Switching Strategies

The strategy can be easily switched at runtime by changing the AuditProcessingConfig
```java
    @Bean
    public BatchOptimizationStrategy batchOptimizationStrategy() {
        return new FirstFitDecreasingStrategy();
    }

```
