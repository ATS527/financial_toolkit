package io.github.ats527.reconciliation.engine;

import java.util.List;

import io.github.ats527.reconciliation.domain.InternalTransaction;
import io.github.ats527.reconciliation.domain.ReconciliationBatchSummary;
import io.github.ats527.reconciliation.domain.SettlementRecord;

public interface ReconciliationEngine {
    /**
     * Reconciles internal transactions against external settlement records.
     * Pure function: does not mutate inputs or perform external I/O.
     */
    ReconciliationBatchSummary reconcile(
        List<InternalTransaction> internalTransactions,
        List<SettlementRecord> settlementRecords
    );
}
