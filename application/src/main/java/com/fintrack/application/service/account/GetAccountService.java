package com.fintrack.application.service.account;

import com.fintrack.application.port.in.account.GetAccountUseCase;
import com.fintrack.application.port.out.account.LoadAccountPort;
import com.fintrack.application.port.out.cache.AccountCachePort;
import com.fintrack.domain.exception.AccountNotFoundException;
import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetAccountService implements GetAccountUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetAccountService.class);

    private final LoadAccountPort loadAccountPort;
    private final AccountCachePort accountCachePort;

    public GetAccountService(
            LoadAccountPort loadAccountPort,
            AccountCachePort accountCachePort) {
        this.loadAccountPort = loadAccountPort;
        this.accountCachePort = accountCachePort;
    }

    @Override
    public Account getAccount(AccountId accountId, UserId ownerId) {
        log.debug("Loading account id={} owner={}", accountId, ownerId);

        // Check cache first — if balance is cached we still load the full account
        // from persistence (cache only stores balance for fast reads)
        return loadAccountPort.loadByIdAndOwner(accountId, ownerId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    @Override
    public List<Account> getAllAccounts(UserId ownerId) {
        log.debug("Loading all accounts for owner={}", ownerId);
        return loadAccountPort.loadAllByOwner(ownerId);
    }
}