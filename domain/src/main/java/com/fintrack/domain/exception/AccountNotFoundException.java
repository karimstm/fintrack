package com.fintrack.domain.exception;

import com.fintrack.domain.model.account.AccountId;

public class AccountNotFoundException extends DomainException {

    public AccountNotFoundException(AccountId id) {
        super("Account not found: " + id);
    }
}