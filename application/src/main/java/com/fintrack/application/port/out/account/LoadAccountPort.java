package com.fintrack.application.port.out.account;

import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

public interface LoadAccountPort {

    Optional<Account> loadById(AccountId accountId);

    Optional<Account> loadByIdAndOwner(AccountId accountId, UserId ownerId);

    List<Account> loadAllByOwner(UserId ownerId);

    boolean existsByIdAndOwner(AccountId accountId, UserId ownerId);
}