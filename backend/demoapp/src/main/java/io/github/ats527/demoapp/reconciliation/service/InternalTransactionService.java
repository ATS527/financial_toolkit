package io.github.ats527.demoapp.reconciliation.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import io.github.ats527.reconciliation.domain.InternalTransaction;

public class InternalTransactionService {
    
    public List<InternalTransaction> getInternalTransactions() {
        return List.of(
            new InternalTransaction("txn101", "ref1", new BigDecimal("100.00"), "USD", Instant.now()),
            new InternalTransaction("txn102", "ref2", new BigDecimal("200.00"), "USD", Instant.now()),
            new InternalTransaction("txn103", "ref3", new BigDecimal("75.50"), "USD", Instant.now())
        );
    }
}
