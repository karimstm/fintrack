package com.fintrack.domain.event;

import com.fintrack.domain.model.budget.BudgetId;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.Category;
import com.fintrack.domain.model.user.UserId;

import java.time.Instant;
import java.util.UUID;

public record BudgetExceededEvent(
        UUID eventId,
        Instant occurredAt,
        BudgetId budgetId,
        UserId ownerId,
        Category category,
        Money limit,
        Money spent,
        BudgetPeriod period
) implements DomainEvent {

    public BudgetExceededEvent(
            BudgetId budgetId,
            UserId ownerId,
            Category category,
            Money limit,
            Money spent,
            BudgetPeriod period) {
        this(UUID.randomUUID(), Instant.now(), budgetId, ownerId, category, limit, spent, period);
    }

    @Override
    public String getEventType() { return "budget.exceeded"; }

    @Override
    public UUID getEventId() { return eventId; }

    @Override
    public Instant getOccurredAt() { return occurredAt; }
}