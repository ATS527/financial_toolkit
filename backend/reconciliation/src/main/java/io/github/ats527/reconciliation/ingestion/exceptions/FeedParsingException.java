package io.github.ats527.reconciliation.ingestion.exceptions;

public class FeedParsingException extends RuntimeException {
    
    private final int lineNumber;

    public FeedParsingException(String message, int lineNumber) {
        super("Line " + lineNumber + ": " + message);
        this.lineNumber = lineNumber;
    }

    public FeedParsingException(String message, int lineNumber, Throwable cause) {
        super("Line " + lineNumber + ": " + message, cause);
        this.lineNumber = lineNumber;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
}