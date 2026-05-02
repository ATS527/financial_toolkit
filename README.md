# Financial Backend Toolkit

Reusable Spring Boot building blocks for fintech and backend teams.

The goal is to create small, dependable infrastructure modules that other developers can reuse in real backend systems.

## Why this exists

Most fintech and backend teams keep rebuilding the same core utilities:

- ledger and accounting logic
- reconciliation workflows
- spreadsheet and CSV import pipelines
- SFTP file ingestion
- scheduled worker jobs
- security helpers for signed requests and webhooks

This project exists to learn those systems deeply while building something that can actually be reused by others.

## Planned modules

### 1. Ledger Engine

The first major module.

Goals:

- double-entry accounting
- journal entries
- account balances
- immutable audit trail
- transaction safety

This is the foundation of the toolkit and the most important place to get the rules right.

### 2. Reconciliation Engine

Generic matching of internal and external records.

Examples:

- exact transaction match
- amount mismatch flagging
- missing record detection
- configurable rule-based outcomes

### 3. File Processing Framework

Reusable ingestion support for CSV and Excel files.

Goals:

- schema validation
- row-level validation errors
- object mapping
- large file handling
- streaming where needed

### 4. SFTP Polling Service

Config-driven file pickup and processing.

Goals:

- connect to SFTP
- fetch files from a configured path
- process automatically on a schedule
- keep processing idempotent

### 5. Job and Worker Framework

Shared execution patterns for background work.

Goals:

- retries
- scheduled execution
- failure tracking
- idempotency guards

### 6. Security Utilities

Common helpers for integration security.

Goals:

- HMAC verification
- webhook signature validation
- replay protection

## Suggested repository direction

As the project grows, the repository can be organized around independent modules:

```text
financial-toolkit/
  ledger-engine/
  reconciliation-engine/
  file-processor/
  sftp-client/
  demo-app/
```

The `demo-app` should remain a thin example application that shows how the reusable pieces are meant to be used.

## Roadmap

### Phase 1

- Ledger engine
- Basic API
- Database-backed persistence
- Balance calculation

### Phase 2

- File processor for CSV and Excel uploads

### Phase 3

- Reconciliation engine

### Phase 4

- SFTP ingestion
- scheduled job support
