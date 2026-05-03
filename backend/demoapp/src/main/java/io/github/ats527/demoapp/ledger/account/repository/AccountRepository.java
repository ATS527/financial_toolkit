package io.github.ats527.demoapp.ledger.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.github.ats527.demoapp.ledger.account.entity.AccountEntity;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    public AccountEntity findById(long id);

    public AccountEntity findByAccountNumber(String accountNumber);

    public boolean existsByAccountNumber(String accountNumber);
}
