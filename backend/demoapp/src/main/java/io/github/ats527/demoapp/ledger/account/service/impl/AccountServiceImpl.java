package io.github.ats527.demoapp.ledger.account.service.impl;

import io.github.ats527.demoapp.ledger.account.dto.CreateAccountRequestDTO;
import io.github.ats527.demoapp.ledger.account.entity.AccountEntity;
import io.github.ats527.demoapp.ledger.account.repository.AccountRepository;
import io.github.ats527.demoapp.ledger.account.service.AccountService;
import io.github.ats527.demoapp.shared.exception.exceptions.BusinessLogicException;

public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;

    @Override
    public AccountEntity createAccount(CreateAccountRequestDTO request) {
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new BusinessLogicException("Account number already exists");
        }

        AccountEntity account = AccountEntity.builder()
            .name(request.name())
            .accountNumber(request.accountNumber())
            .type(request.type())
            .build();

        return accountRepository.save(account);
    }
    
}
