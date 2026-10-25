package io.github.ats527.reconciliation.domain;

public enum MatchStatus {
    // 1. Everything balances perfectly: IDs, gross amount, and fees match
    EXACT_MATCH,
    // 2. IDs match, but internal amount != external gross amount (e.g., partial charge, currency slip)
    AMOUNT_MISMATCH,
    // 3. IDs and gross match, but the gateway fee is different from the expected contract
    FEE_DISCREPANCY,
    // 4. CRITICAL BREAK: Gateway charged customer, but our DB has NO record (dropped webhook)
    MISSING_INTERNAL,
    // 5. REVENUE LOSS BREAK: Our DB says SUCCESS, but gateway NEVER settled the money
    MISSING_EXTERNAL
}
