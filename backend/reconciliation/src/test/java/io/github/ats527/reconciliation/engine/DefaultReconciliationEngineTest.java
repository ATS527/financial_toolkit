package io.github.ats527.reconciliation.engine;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.ats527.reconciliation.domain.InternalTransaction;
import io.github.ats527.reconciliation.domain.MatchStatus;
import io.github.ats527.reconciliation.domain.ReconciliationBatchSummary;
import io.github.ats527.reconciliation.domain.ReconciliationRecord;
import io.github.ats527.reconciliation.domain.SettlementRecord;
import io.github.ats527.reconciliation.engine.impl.DefaultReconciliationEngine;

public class DefaultReconciliationEngineTest {

    private DefaultReconciliationEngine reconciliationEngine = new DefaultReconciliationEngine();
    
    @Test 
    void shouldExactMatchCleanTransactions() {

        List<InternalTransaction> internalTransactions = List.of(
            new InternalTransaction("txn1", "ref1", new BigDecimal("100.00"), "USD", Instant.now()),
            new InternalTransaction("txn2", "ref2", new BigDecimal("200.00"), "USD", Instant.now())
        );

        List<SettlementRecord> settlementRecords = List.of(
            new SettlementRecord("set1", "ref1", new BigDecimal("100.00"), new BigDecimal("0.00"), new BigDecimal("100.00"), "USD", Instant.now()),
            new SettlementRecord("set2", "ref2", new BigDecimal("200.00"), new BigDecimal("0.00"), new BigDecimal("200.00"), "USD", Instant.now())
        );

        ReconciliationBatchSummary summary = reconciliationEngine.reconcile(internalTransactions, settlementRecords);

        assert summary.exactMatchCount() == 2;
        assert summary.mismatchCount() == 0;
        assert summary.totalDiscrepancyAmount().compareTo(BigDecimal.ZERO) == 0;
    }

    @Test
    void shouldDetectAmountAndCurrencyMismatch() {
        
        List<InternalTransaction> internalTransactions = List.of(
            new InternalTransaction("txn1", "ref1", new BigDecimal("100.00"), "USD", Instant.now()),
            new InternalTransaction("txn2", "ref2", new BigDecimal("200.00"), "USD", Instant.now())
        );

        List<SettlementRecord> settlementRecords = List.of(
            new SettlementRecord("set1", "ref1", new BigDecimal("100.00"), new BigDecimal("0.00"), new BigDecimal("100.00"), "USD", Instant.now()),
            new SettlementRecord("set2", "ref2", new BigDecimal("250.00"), new BigDecimal("0.00"), new BigDecimal("250.00"), "EUR", Instant.now())
        );

        ReconciliationBatchSummary summary = reconciliationEngine.reconcile(internalTransactions, settlementRecords);

        assert summary.exactMatchCount() == 1;
        assert summary.mismatchCount() == 1;
        assert summary.totalDiscrepancyAmount().compareTo(new BigDecimal("50.00")) == 0;
    }

    @Test
    void shouldDetectMissingExternalRecord() {
        
        List<InternalTransaction> internalTransactions = List.of(
            new InternalTransaction("txn1", "ref1", new BigDecimal("100.00"), "USD", Instant.now()),
            new InternalTransaction("txn2", "ref2", new BigDecimal("200.00"), "USD", Instant.now())
        );

        List<SettlementRecord> settlementRecords = List.of(
            new SettlementRecord("set1", "ref1", new BigDecimal("100.00"), new BigDecimal("0.00"), new BigDecimal("100.00"), "USD", Instant.now())
        );

        ReconciliationBatchSummary summary = reconciliationEngine.reconcile(internalTransactions, settlementRecords);

        assert summary.exactMatchCount() == 1;
        assert summary.mismatchCount() == 1;
        assert summary.totalDiscrepancyAmount().compareTo(new BigDecimal("200.00")) == 0;
        ReconciliationRecord missingRecord = summary.records().stream()
            .filter(record -> record.internalTransactionId().equalsIgnoreCase("txn2"))
            .findFirst()
            .orElse(null);

        assert missingRecord != null;
        assert missingRecord.status() == MatchStatus.MISSING_EXTERNAL;
        assert summary.totalDiscrepancyAmount().compareTo(missingRecord.amountDifference()) == 0;
    }

    @Test 
    void shouldDetectMissingInternalRecord() {
        
        List<InternalTransaction> internalTransactions = List.of(
            new InternalTransaction("txn1", "ref1", new BigDecimal("100.00"), "USD", Instant.now())
        );

        List<SettlementRecord> settlementRecords = List.of(
            new SettlementRecord("set1", "ref1", new BigDecimal("100.00"), new BigDecimal("0.00"), new BigDecimal("100.00"), "USD", Instant.now()),
            new SettlementRecord("set2", "ref2", new BigDecimal("200.00"), new BigDecimal("0.00"), new BigDecimal("200.00"), "USD", Instant.now())
        );

        ReconciliationBatchSummary summary = reconciliationEngine.reconcile(internalTransactions, settlementRecords);

        assert summary.exactMatchCount() == 1;
        assert summary.mismatchCount() == 1;
        ReconciliationRecord missingRecord = summary.records().stream()
            .filter(record -> record.externalReferenceId().equalsIgnoreCase("ref2"))
            .findFirst()
            .orElse(null);

        assert missingRecord != null;
        assert missingRecord.status() == MatchStatus.MISSING_INTERNAL;
        assert summary.totalDiscrepancyAmount().compareTo(missingRecord.amountDifference()) == 0;
    }

    @Test 
    void shouldHandleMixedBatchCorrectly() {
        
        List<InternalTransaction> internalTransactions = List.of(
            new InternalTransaction("txn1", "ref1", new BigDecimal("100.00"), "USD", Instant.now()),
            new InternalTransaction("txn2", "ref2", new BigDecimal("200.00"), "USD", Instant.now()),
            new InternalTransaction("txn3", "ref3", new BigDecimal("300.00"), "USD", Instant.now())
        );

        List<SettlementRecord> settlementRecords = List.of(
            new SettlementRecord("set1", "ref1", new BigDecimal("100.00"), new BigDecimal("0.00"), new BigDecimal("100.00"), "USD", Instant.now()),
            new SettlementRecord("set2", "ref2", new BigDecimal("250.00"), new BigDecimal("0.00"), new BigDecimal("250.00"), "EUR", Instant.now()),
            new SettlementRecord("set3", "ref4", new BigDecimal("400.00"), new BigDecimal("0.00"), new BigDecimal("400.00"), "USD", Instant.now())
        );

        ReconciliationBatchSummary summary = reconciliationEngine.reconcile(internalTransactions, settlementRecords);

        assert summary.exactMatchCount() == 1;
        assert summary.mismatchCount() == 3;
        assert summary.totalDiscrepancyAmount().compareTo(new BigDecimal("750.00")) == 0;
    }
}
