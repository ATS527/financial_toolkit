package io.github.ats527.demoapp.ledger.account.service;

import io.github.ats527.demoapp.ledger.account.dto.CreateAccountRequestDTO;
import io.github.ats527.demoapp.ledger.account.dto.CreateAccountResponseDTO;
import io.github.ats527.demoapp.shared.exception.exceptions.BusinessLogicException;

public interface AccountService {
    CreateAccountResponseDTO createAccount(CreateAccountRequestDTO request) throws BusinessLogicException;
}
