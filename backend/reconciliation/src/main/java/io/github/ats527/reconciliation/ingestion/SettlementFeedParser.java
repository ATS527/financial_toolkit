package io.github.ats527.reconciliation.ingestion;

import java.io.InputStream;
import java.util.List;

import io.github.ats527.reconciliation.domain.SettlementRecord;

public interface SettlementFeedParser {
    /**
     * Parses a raw stream (e.g. CSV or JSON from file/network)
     * into normalized SettlementRecord domain objects.
     */
    List<SettlementRecord> parse(InputStream inputStream);
}
