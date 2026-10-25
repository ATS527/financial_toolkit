package io.github.ats527.reconciliation.domain;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents the internal system's authoritative record of an initiated transaction.
 * This is typically populated from your Order Service or Internal Ledger database.
 *
 * @param transactionId
 *        Our internal system's unique identifier (e.g., UUID: "txn_01HXYZ...").
 *        Used for tracing orders and customer receipts internally.
 *
 * @param gatewayReferenceId
 *        The external reference key assigned by the Payment Gateway during authorization/capture.
 *        Example: Stripe's "pi_3Mtwx..." or "ch_3Mtwx...", or Razorpay's "pay_K123456...".
 *        This is the primary foreign key used to pair internal and external records.
 *
 * @param amount
 *        The transaction gross amount authorized/captured from the customer.
 *        Always represented with fixed precision (e.g., scale 2 for USD, scale 0 for JPY).
 *        Never negative for a charge.
 *
 * @param currency
 *        Three-letter uppercase ISO-4217 currency code (e.g., "USD", "EUR", "INR").
 *        Crucial for multi-currency platforms to prevent cross-currency false matches.
 *
 * @param timestamp
 *        The exact point in time (in UTC) when the transaction was successfully
 *        captured by the internal service. Used for sliding-window and latency reconciliation.
 */
public record InternalTransaction(
    String transactionId,

    String gatewayReferenceId,

    BigDecimal amount,

    String currency,

    Instant timestamp
) {

}
