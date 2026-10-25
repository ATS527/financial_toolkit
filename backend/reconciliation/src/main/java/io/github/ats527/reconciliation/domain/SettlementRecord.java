package io.github.ats527.reconciliation.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a single line item ingested from an external Payment Gateway's
 * settlement or payout batch report (e.g., Stripe Payout Reconciliation CSV, Adyen Settlement Details).
 *
 * @param settlementId
 *        The gateway's unique identifier for this specific settlement entry/line-item
 *        (e.g., "set_987654321"). Used to ensure idempotent ingestion of settlement files.
 *
 * @param externalReferenceId
 *        The gateway transaction reference that maps back to the merchant's charge.
 *        Matches {@link InternalTransaction#gatewayReferenceId} (e.g., "ch_3Mtwx...").
 *
 * @param grossAmount
 *        The full amount charged to the end-customer before fees or deductions.
 *        Should match {@link InternalTransaction#amount}.
 *
 * @param feeAmount
 *        The payment processor's deduction (interchange, scheme, and gateway fees).
 *        Example: on a $100.00 charge, fee might be $3.20.
 *
 * @param netAmount
 *        The actual amount deposited into the company's bank account.
 *        Accounting invariant: grossAmount - feeAmount == netAmount.
 *
 * @param currency
 *        The settlement currency (ISO-4217). If multi-currency conversion occurred at the gateway,
 *        this represents the currency received by the merchant.
 *
 * @param settledAt
 *        The exact UTC timestamp when funds were officially cleared and settled by the gateway.
 *        Typically T+1 to T+3 business days after the initial transaction timestamp.
 */
public record SettlementRecord(
    String settlementId,

    String externalReferenceId,

    BigDecimal grossAmount,

    BigDecimal feeAmount,

    BigDecimal netAmount,

    String currency,

    Instant settledAt
) {

}
