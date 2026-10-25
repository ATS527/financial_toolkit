package io.github.ats527.demoapp.reconciliation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.ats527.reconciliation.engine.ReconciliationEngine;
import io.github.ats527.reconciliation.engine.impl.DefaultReconciliationEngine;
import io.github.ats527.reconciliation.ingestion.SettlementFeedParser;
import io.github.ats527.reconciliation.ingestion.impl.CsvSettlementFeedParser;

@Configuration 
public class ReconciliationConfig {
    
    @Bean 
    public ReconciliationEngine reconciliationEngine() {
        return new DefaultReconciliationEngine();
    }

    @Bean 
    public SettlementFeedParser settlementFeedParser() {
        return new CsvSettlementFeedParser();
    }
}
