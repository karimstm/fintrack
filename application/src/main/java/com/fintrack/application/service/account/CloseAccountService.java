package com.fintrack.application.service.account;

import com.fintrack.application.port.in.account.CloseAccountUseCase;
import com.fintrack.application.port.out.account.DeleteAccountPort;
import com.fintrack.application.port.out.account.LoadAccountPort;
import com.fintrack.application.port.out.cache.AccountCachePort;
import com.fintrack.application.port.out.notification.DomainEventPublisherPort;
import com.fintrack.domain.exception.AccountNotFoundException;
import com.fintrack.domain.model.account.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CloseAccountService implements CloseAccountUseCase {

    private static final Logger log = LoggerFactory.getLogger(CloseAccountService.class);

    private final LoadAccountPort loadAccountPort;
    private final DeleteAccountPort deleteAccountPort;
    private final AccountCachePort accountCachePort;
    private final DomainEventPublisherPort eventPublisher;

    public CloseAccountService(
            LoadAccountPort loadAccountPort,
            DeleteAccountPort deleteAccountPort,
            AccountCachePort accountCachePort,
            DomainEventPublisherPort eventPublisher) {
        this.loadAccountPort = loadAccountPort;
        this.deleteAccountPort = deleteAccountPort;
        this.accountCachePort = accountCachePort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void closeAccount(Command command) {
        log.info("Closing account id={} owner={}", command.accountId(), command.ownerId());

        Account account = loadAccountPort
                .loadByIdAndOwner(command.accountId(), command.ownerId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        // Domain enforces business rule: cannot close with non-zero balance
        account.close();

        deleteAccountPort.deleteByIdAndOwner(command.accountId(), command.ownerId());

        // Evict from cache
        accountCachePort.evictBalance(command.accountId());

        eventPublisher.publishAll(account.pullDomainEvents());

        log.info("Account closed successfully id={}", command.accountId());
    }
}