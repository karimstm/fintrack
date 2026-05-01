package com.fintrack.application.port.out.cache;

import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.shared.Money;

import java.util.Optional;

public interface AccountCachePort {

    void cacheBalance(AccountId accountId, Money balance);

    Optional<Money> loadCachedBalance(AccountId accountId);

    void evictBalance(AccountId accountId);
}