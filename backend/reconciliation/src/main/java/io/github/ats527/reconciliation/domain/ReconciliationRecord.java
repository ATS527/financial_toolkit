package io.github.ats527.reconciliation.domain;

import java.math.BigDecimal;

/**
 * The immutable result of reconciling a paired (or orphaned) transaction.
 * Acts as an audit trail for financial compliance and operations dashboards.
 *
 * @param reconciliationId
 *        Unique audit identifier for this reconciliation result record (e.g., UUID).
 *
 * @param internalTransactionId
 *        The ID of the internal transaction being reconciled.
 *        Nullable: will be NULL if {@code status == MatchStatus.MISSING_INTERNAL} (ghost charge).
 *
 * @param externalReferenceId
 *        The payment gateway reference from the settlement feed.
 *        Nullable: will be NULL if {@code status == MatchStatus.MISSING_EXTERNAL} (missing payout).
 *
 * @param status
 *        The outcome classification of the match (e.g., EXACT_MATCH, AMOUNT_MISMATCH, FEE_DISCREPANCY).
 *
 * @param amountDifference
 *        The numerical discrepancy between external gross and internal captured amount.
 *        Formula: {@code settlementGross - internalAmount}.
 *        Zero for EXACT_MATCH. Positive means over-settled; negative means under-settled.
 *
 * @param feeDifference
 *        The difference between the actual gateway fee and the expected contract fee.
 *        Zero if fee matching is within contract tolerance.
 *
 * @param discrepancyReason
 *        Human-readable operational notes explaining any break (e.g.,
 *        "Gateway fee $3.50 exceeds contracted rate $3.20 by $0.30").
 *        Null or empty for clean EXACT_MATCH.
 *
 * @param reconciledAt
 *        The exact UTC timestamp when this reconciliation job run evaluated the record.
 */
public record ReconciliationRecord(
    String reconciliationId,

    String internalTransactionId,

    String externalReferenceId,

    MatchStatus status,

    BigDecimal amountDifference,

    String discrepancyReason
) {

}
