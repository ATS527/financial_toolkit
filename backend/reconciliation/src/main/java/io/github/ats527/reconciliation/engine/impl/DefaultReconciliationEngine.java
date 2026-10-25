package io.github.ats527.reconciliation.engine.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.github.ats527.reconciliation.domain.InternalTransaction;
import io.github.ats527.reconciliation.domain.MatchStatus;
import io.github.ats527.reconciliation.domain.ReconciliationBatchSummary;
import io.github.ats527.reconciliation.domain.ReconciliationRecord;
import io.github.ats527.reconciliation.domain.SettlementRecord;
import io.github.ats527.reconciliation.engine.ReconciliationEngine;

public class DefaultReconciliationEngine implements ReconciliationEngine {

    @Override
    public ReconciliationBatchSummary reconcile(
        List<InternalTransaction> internalTransactions,
        List<SettlementRecord> settlementRecords
    ) {
        Map<String, SettlementRecord> externalRefIdToSettlementRecordMap = new HashMap<>();

        settlementRecords.forEach((settlementRecord -> 
            externalRefIdToSettlementRecordMap.put(settlementRecord.externalReferenceId(), settlementRecord)
        ));

        List<ReconciliationRecord> reconciliationRecords = new ArrayList<>();

        int exactMatchCount = 0;
        int mismatchCount = 0;
        BigDecimal totalDiscrepancyAmount = BigDecimal.ZERO;

        for (InternalTransaction internalTransaction : internalTransactions) {
            SettlementRecord settlementRecord = externalRefIdToSettlementRecordMap.get(internalTransaction.gatewayReferenceId());

            ReconciliationRecord matchRecord;

            if (settlementRecord != null) {
                boolean amountsMatch = settlementRecord.grossAmount().compareTo(internalTransaction.amount()) == 0;
                boolean currenciesMatch = settlementRecord.currency().equalsIgnoreCase(internalTransaction.currency());

                if (amountsMatch && currenciesMatch) {
                    matchRecord = new ReconciliationRecord(
                        UUID.randomUUID().toString(),
                        internalTransaction.transactionId(),
                        settlementRecord.externalReferenceId(),
                        MatchStatus.EXACT_MATCH,
                        BigDecimal.ZERO,
                        null
                    );

                    exactMatchCount++;
                } else {
                    BigDecimal discrepancyAmount = settlementRecord.grossAmount().subtract(internalTransaction.amount());

                    matchRecord = new ReconciliationRecord(
                        UUID.randomUUID().toString(),
                        internalTransaction.transactionId(),
                        settlementRecord.externalReferenceId(),
                        MatchStatus.AMOUNT_MISMATCH,
                        discrepancyAmount,
                        "Amounts differ"
                    );

                    mismatchCount++;
                    totalDiscrepancyAmount = totalDiscrepancyAmount.add(discrepancyAmount.abs());
                }

                externalRefIdToSettlementRecordMap.remove(settlementRecord.externalReferenceId());
            } else {
                matchRecord = new ReconciliationRecord(
                    UUID.randomUUID().toString(),
                    internalTransaction.transactionId(),
                    null,
                    MatchStatus.MISSING_EXTERNAL,
                    internalTransaction.amount(),
                    "missing external record"
                );

                mismatchCount++;
                totalDiscrepancyAmount = totalDiscrepancyAmount.add(internalTransaction.amount());
            }

            reconciliationRecords.add(matchRecord);
        }

        for (SettlementRecord settlementRecord : externalRefIdToSettlementRecordMap.values()) {
            ReconciliationRecord reconciliationRecord = new ReconciliationRecord(
                UUID.randomUUID().toString(),
                null,
                settlementRecord.externalReferenceId(),
                MatchStatus.MISSING_INTERNAL,
                settlementRecord.grossAmount(),
                "missing internal record"
            );

            reconciliationRecords.add(reconciliationRecord);

            mismatchCount++;
            totalDiscrepancyAmount = totalDiscrepancyAmount.add(settlementRecord.grossAmount());
        }

        ReconciliationBatchSummary reconciliationBatchSummary = new ReconciliationBatchSummary(
            UUID.randomUUID().toString(),
            Instant.now(),
            internalTransactions.size(),
            settlementRecords.size(),
            exactMatchCount,
            mismatchCount,
            totalDiscrepancyAmount,
            reconciliationRecords
        );

        return reconciliationBatchSummary;
    }
}
