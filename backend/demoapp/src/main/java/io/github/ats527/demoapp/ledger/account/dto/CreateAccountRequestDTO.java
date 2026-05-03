package io.github.ats527.demoapp.ledger.account.dto;

import io.github.ats527.demoapp.ledger.account.enums.AccountTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAccountRequestDTO(
    
    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must be at most 100 characters")
    String name,

    @NotBlank(message = "Account number is required")
    @Size(max = 50, message = "Account number must be at most 50 characters")
    String accountNumber,

    @NotNull(message = "Account type is required")
    AccountTypeEnum type
) {    
}
