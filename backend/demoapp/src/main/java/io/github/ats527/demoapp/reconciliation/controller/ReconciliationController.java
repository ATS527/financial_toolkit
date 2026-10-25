package io.github.ats527.demoapp.reconciliation.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.github.ats527.demoapp.reconciliation.service.InternalTransactionService;
import io.github.ats527.reconciliation.domain.InternalTransaction;
import io.github.ats527.reconciliation.domain.ReconciliationBatchSummary;
import io.github.ats527.reconciliation.domain.SettlementRecord;
import io.github.ats527.reconciliation.engine.ReconciliationEngine;
import io.github.ats527.reconciliation.ingestion.SettlementFeedParser;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping("/api/reconciliation")
@RequiredArgsConstructor 
public class ReconciliationController {
    
    private final ReconciliationEngine reconciliationEngine;
    private final SettlementFeedParser settlementFeedParser;
    private final InternalTransactionService internalTransactionService;

    @PostMapping(value = "/run", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> runReconciliation(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        List<SettlementRecord> settlementRecords = settlementFeedParser.parse(file.getInputStream());
        List<InternalTransaction> internalTransactions = internalTransactionService.getInternalTransactions();
        ReconciliationBatchSummary summary = reconciliationEngine.reconcile(internalTransactions, settlementRecords);

        return ResponseEntity.ok().body(summary);
    }
}
