package io.github.ats527.demoapp.ledger.account.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.ats527.demoapp.ledger.account.dto.CreateAccountRequestDTO;
import io.github.ats527.demoapp.ledger.account.dto.CreateAccountResponseDTO;
import io.github.ats527.demoapp.ledger.account.entity.AccountEntity;
import io.github.ats527.demoapp.ledger.account.enums.AccountStatusEnum;
import io.github.ats527.demoapp.ledger.account.enums.AccountTypeEnum;
import io.github.ats527.demoapp.ledger.account.repository.AccountRepository;
import io.github.ats527.demoapp.shared.exception.exceptions.BusinessLogicException;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void shouldCreateAccountWhenAccountNumberDoesNotExist() {
        // Arrage
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(
            "ACC001",
            "656575758585",
            AccountTypeEnum.ASSET
        );

        when(accountRepository.existsByAccountNumber(request.accountNumber())).thenReturn(false);

        AccountEntity accountEntity = AccountEntity.builder()
            .id(1L)
            .accountNumber(request.accountNumber())
            .type(request.accountType())
            .status(AccountStatusEnum.ACTIVE)
            .name(request.name())
            .build();

        when(accountRepository.save(any(AccountEntity.class))).thenReturn(accountEntity);

        // Act
        CreateAccountResponseDTO createdAccount = accountService.createAccount(request);

        // Assert
        assertThat(createdAccount.accountNumber()).isEqualTo(request.accountNumber());
        assertThat(createdAccount.accountType()).isEqualTo(request.accountType());
        assertThat(createdAccount.name()).isEqualTo(request.name());

        verify(accountRepository).existsByAccountNumber(request.accountNumber());
        verify(accountRepository).save(any(AccountEntity.class));
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    void shouldThrowWhenAccountNumberAlreadyExists() {
        // Arrage
        CreateAccountRequestDTO request = new CreateAccountRequestDTO(
            "ACC001",
            "656575758585",
            AccountTypeEnum.ASSET
        );

        when(accountRepository.existsByAccountNumber(request.accountNumber())).thenReturn(true);

        BusinessLogicException exception = assertThrows(
            BusinessLogicException.class, 
            () -> accountService.createAccount(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Account number already exists");
        verify(accountRepository).existsByAccountNumber(request.accountNumber());
        verify(accountRepository, never()).save(any());
    }
}