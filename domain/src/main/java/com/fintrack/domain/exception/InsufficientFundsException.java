package com.fintrack.domain.exception;

import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.shared.Money;

public class InsufficientFundsException extends DomainException {

    private final AccountId accountId;
    private final Money currentBalance;
    private final Money requestedAmount;

    public InsufficientFundsException(AccountId accountId, Money currentBalance, Money requestedAmount) {
        super("Insufficient funds in account %s: balance is %s, requested %s"
                .formatted(accountId, currentBalance, requestedAmount));
        this.accountId = accountId;
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }

    public AccountId getAccountId() { return accountId; }
    public Money getCurrentBalance() { return currentBalance; }
    public Money getRequestedAmount() { return requestedAmount; }
}