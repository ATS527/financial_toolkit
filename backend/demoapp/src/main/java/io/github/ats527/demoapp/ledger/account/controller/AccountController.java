package io.github.ats527.demoapp.ledger.account.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.ats527.demoapp.ledger.account.dto.CreateAccountRequestDTO;
import io.github.ats527.demoapp.ledger.account.dto.CreateAccountResponseDTO;
import io.github.ats527.demoapp.ledger.account.service.AccountService;
import io.github.ats527.demoapp.shared.constants.Routes;
import io.github.ats527.demoapp.shared.response.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping(Routes.ACCOUNT)
public class AccountController {

    private final AccountService accountService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<CreateAccountResponseDTO>> createAccount(
        @Valid @RequestBody CreateAccountRequestDTO request
    ) {
        CreateAccountResponseDTO account = accountService.createAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of("Account Created", account));
    }
}
