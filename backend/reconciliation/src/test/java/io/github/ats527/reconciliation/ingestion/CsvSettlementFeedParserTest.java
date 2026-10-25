package io.github.ats527.reconciliation.ingestion;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.ats527.reconciliation.domain.SettlementRecord;
import io.github.ats527.reconciliation.ingestion.exceptions.FeedParsingException;
import io.github.ats527.reconciliation.ingestion.impl.CsvSettlementFeedParser;

public class CsvSettlementFeedParserTest {
    
    private CsvSettlementFeedParser parser = new CsvSettlementFeedParser();

    @Test 
    void shouldParseValidCsvSuccessfully() {
        String csvData = "settlementId,referenceId,grossAmount,fee,netAmount,currency,timestamp\n" +
                         "set1,ref1,100.00,0.00,100.00,USD,2023-01-01T10:00:00Z\n" +
                         "set2,ref2,200.00,0.00,200.00,USD,2023-01-02T10:00:00Z";
        
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        List<SettlementRecord> records = parser.parse(inputStream);

        assert records.size() == 2;
        assert records.get(0).settlementId().equals("set1");
        assert records.get(1).settlementId().equals("set2");
        assert records.get(0).externalReferenceId().equals("ref1");
        assert records.get(1).externalReferenceId().equals("ref2");
    }

    @Test 
    void shouldThrowExceptionWhenColumnMissing() {
        String csvData = "settlementId,referenceId,grossAmount,fee,netAmount,currency,timestamp\n" +
                         "set1,ref1,100.00,0.00,100.00,USD\n" +
                         "set2,ref2,200.00,0.00,200.00,USD";
        
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        FeedParsingException exception = assertThrows(FeedParsingException.class, () -> parser.parse(inputStream));

        assert exception.getLineNumber() == 2;
        assert exception.getMessage().contains("Line 2: Expected 7 columns, but found 6");
    }

    @Test 
    void shouldThrowExceptionWhenAccountingInvariantViolated() {
        String csvData = "settlementId,referenceId,grossAmount,fee,netAmount,currency,timestamp\n" +
                         "set1,ref1,100.00,10.00,80.00,USD,2023-01-01T10:00:00Z\n" +
                         "set2,ref2,200.00,20.00,190.00,USD,2023-01-02T10:00:00Z";
        
        InputStream inputStream = new ByteArrayInputStream(csvData.getBytes(StandardCharsets.UTF_8));

        FeedParsingException exception = assertThrows(FeedParsingException.class, () -> parser.parse(inputStream));

        assert exception.getLineNumber() == 2;
        assert exception.getMessage().contains("Line 2: Gross amount minus fee does not equal net amount");
    }

    @Test 
    void shouldThrowExceptionWhenInputStreamIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> parser.parse(null));

        assert exception.getMessage().contains("Input stream cannot be null");
    }
}
