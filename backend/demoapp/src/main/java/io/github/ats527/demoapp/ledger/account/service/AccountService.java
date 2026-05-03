package io.github.ats527.demoapp.ledger.account.service;

import io.github.ats527.demoapp.ledger.account.dto.CreateAccountRequestDTO;
import io.github.ats527.demoapp.ledger.account.entity.AccountEntity;
import io.github.ats527.demoapp.shared.exception.exceptions.BusinessLogicException;

public interface AccountService {
    AccountEntity createAccount(CreateAccountRequestDTO request) throws BusinessLogicException;
}
