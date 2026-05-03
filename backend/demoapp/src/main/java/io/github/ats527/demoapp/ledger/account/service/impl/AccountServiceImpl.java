package io.github.ats527.demoapp.ledger.account.service.impl;

import org.springframework.stereotype.Service;

import io.github.ats527.demoapp.ledger.account.dto.CreateAccountRequestDTO;
import io.github.ats527.demoapp.ledger.account.dto.CreateAccountResponseDTO;
import io.github.ats527.demoapp.ledger.account.entity.AccountEntity;
import io.github.ats527.demoapp.ledger.account.repository.AccountRepository;
import io.github.ats527.demoapp.ledger.account.service.AccountService;
import io.github.ats527.demoapp.shared.exception.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public CreateAccountResponseDTO createAccount(CreateAccountRequestDTO request) {
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new BusinessLogicException("Account number already exists");
        }

        AccountEntity account = AccountEntity.builder()
            .name(request.name())
            .accountNumber(request.accountNumber())
            .type(request.type())
            .build();

        accountRepository.save(account);

        return new CreateAccountResponseDTO(
            account.getName(),
            account.getAccountNumber(),
            account.getType()
        );
    }
    
}
