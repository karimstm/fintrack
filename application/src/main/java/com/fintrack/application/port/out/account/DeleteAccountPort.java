package com.fintrack.application.port.out.account;

import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.user.UserId;

public interface DeleteAccountPort {

    void deleteByIdAndOwner(AccountId accountId, UserId ownerId);
}