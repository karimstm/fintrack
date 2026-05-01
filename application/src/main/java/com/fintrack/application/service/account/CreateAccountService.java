package com.fintrack.application.service.account;

import com.fintrack.application.port.in.account.CreateAccountUseCase;
import com.fintrack.application.port.out.account.SaveAccountPort;
import com.fintrack.application.port.out.notification.DomainEventPublisherPort;
import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.shared.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAccountService implements CreateAccountUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateAccountService.class);

    private final SaveAccountPort saveAccountPort;
    private final DomainEventPublisherPort eventPublisher;

    public CreateAccountService(
            SaveAccountPort saveAccountPort,
            DomainEventPublisherPort eventPublisher) {
        this.saveAccountPort = saveAccountPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Account createAccount(Command command) {
        log.info("Creating account for owner={} type={}", command.ownerId(), command.type());

        Money initialBalance = Money.of(
                command.initialBalance(),
                command.currency()
        );

        Account account = Account.open(
                command.ownerId(),
                command.type(),
                command.name(),
                initialBalance
        );

        Account saved = saveAccountPort.save(account);

        // Drain and publish any domain events raised during creation
        eventPublisher.publishAll(saved.pullDomainEvents());

        log.info("Account created successfully id={}", saved.getId());
        return saved;
    }
}