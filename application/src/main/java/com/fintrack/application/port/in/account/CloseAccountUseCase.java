package com.fintrack.application.port.in.account;

import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.user.UserId;

import java.util.Objects;

public interface CloseAccountUseCase {

    void closeAccount(Command command);

    record Command(AccountId accountId, UserId ownerId) {
        public Command {
            Objects.requireNonNull(accountId, "accountId must not be null");
            Objects.requireNonNull(ownerId, "ownerId must not be null");
        }
    }
}