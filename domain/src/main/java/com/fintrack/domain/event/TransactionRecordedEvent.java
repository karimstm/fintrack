package com.fintrack.domain.event;

import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.TransactionId;
import com.fintrack.domain.model.transaction.TransactionType;

import java.time.Instant;
import java.util.UUID;

public record TransactionRecordedEvent(
        UUID eventId,
        Instant occurredAt,
        AccountId accountId,
        TransactionId transactionId,
        Money amount,
        TransactionType transactionType
) implements DomainEvent {

    public TransactionRecordedEvent(
            AccountId accountId,
            TransactionId transactionId,
            Money amount,
            TransactionType transactionType) {
        this(UUID.randomUUID(), Instant.now(), accountId, transactionId, amount, transactionType);
    }

    @Override
    public String getEventType() { return "transaction.recorded"; }

    @Override
    public UUID getEventId() { return eventId; }

    @Override
    public Instant getOccurredAt() { return occurredAt; }
}