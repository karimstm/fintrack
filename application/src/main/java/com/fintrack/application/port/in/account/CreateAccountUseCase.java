package com.fintrack.application.port.in.account;

import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountType;
import com.fintrack.domain.model.user.UserId;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public interface CreateAccountUseCase {

    Account createAccount(Command command);

    record Command(
            UserId ownerId,
            AccountType type,
            String name,
            BigDecimal initialBalance,
            Currency currency
    ) {
        public Command {
            Objects.requireNonNull(ownerId, "ownerId must not be null");
            Objects.requireNonNull(type, "type must not be null");
            Objects.requireNonNull(name, "name must not be null");
            Objects.requireNonNull(initialBalance, "initialBalance must not be null");
            Objects.requireNonNull(currency, "currency must not be null");
            if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
            if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("initialBalance must not be negative");
            }
        }
    }
}