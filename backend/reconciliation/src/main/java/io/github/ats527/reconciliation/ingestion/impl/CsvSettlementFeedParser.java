package io.github.ats527.reconciliation.ingestion.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import io.github.ats527.reconciliation.domain.SettlementRecord;
import io.github.ats527.reconciliation.ingestion.SettlementFeedParser;
import io.github.ats527.reconciliation.ingestion.exceptions.FeedParsingException;

public class CsvSettlementFeedParser implements SettlementFeedParser {

    @Override
    public List<SettlementRecord> parse(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            List<SettlementRecord> settlementRecords = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || lineNumber == 1) {
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length != 7) {
                    throw new FeedParsingException("Expected 7 columns, but found " + fields.length, lineNumber);
                }

                BigDecimal grossAmount = new BigDecimal(fields[2].trim());
                BigDecimal fee = new BigDecimal(fields[3].trim());
                BigDecimal netAmount = new BigDecimal(fields[4].trim());

                if (grossAmount.subtract(fee).compareTo(netAmount) != 0) {
                    throw new FeedParsingException("Gross amount minus fee does not equal net amount", lineNumber);
                }
                
                SettlementRecord record = new SettlementRecord(
                    fields[0].trim(), // settlementId
                    fields[1].trim(), // referenceId
                    grossAmount,
                    fee,
                    netAmount,
                    fields[5].trim(), // currency
                    Instant.parse(fields[6].trim()) // timestamp
                );

                settlementRecords.add(record);
            }

            return settlementRecords;
        } catch (FeedParsingException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing CSV feed", e);
        }
    }
    
}
