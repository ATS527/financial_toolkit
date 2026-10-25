package io.github.ats527.reconciliation.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * High-level audit summary of an entire reconciliation execution window.
 */
public record ReconciliationBatchSummary(
    String batchId,
    Instant executedAt,
    int totalInternalRecords,
    int totalSettlementRecords,
    int exactMatchCount,
    int mismatchCount,
    BigDecimal totalDiscrepancyAmount,
    List<ReconciliationRecord> records
) {
    
}
