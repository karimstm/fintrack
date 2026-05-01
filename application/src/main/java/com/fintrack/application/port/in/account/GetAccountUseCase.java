package com.fintrack.application.port.in.account;

import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.user.UserId;

import java.util.List;

public interface GetAccountUseCase {

    Account getAccount(AccountId accountId, UserId ownerId);

    List<Account> getAllAccounts(UserId ownerId);
}