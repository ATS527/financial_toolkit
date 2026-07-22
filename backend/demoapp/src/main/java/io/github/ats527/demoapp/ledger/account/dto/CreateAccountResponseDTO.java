package io.github.ats527.demoapp.ledger.account.dto;

import io.github.ats527.demoapp.ledger.account.enums.AccountTypeEnum;

public record CreateAccountResponseDTO(
    String name,
    String accountNumber,
    AccountTypeEnum accountType
) {
}
