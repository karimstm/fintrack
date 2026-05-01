package com.fintrack.domain.model.budget;

import com.fintrack.domain.event.BudgetExceededEvent;
import com.fintrack.domain.exception.BudgetExceededException;
import com.fintrack.domain.model.shared.AggregateRoot;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.Category;
import com.fintrack.domain.model.user.UserId;

import java.time.Instant;
import java.util.Objects;

public class Budget extends AggregateRoot {

    private final BudgetId id;
    private final UserId ownerId;
    private final Category category;
    private Money limit;
    private Money spent;
    private final BudgetPeriod period;
    private final Instant createdAt;
    private Instant updatedAt;

    private Budget(
            BudgetId id,
            UserId ownerId,
            Category category,
            Money limit,
            Money spent,
            BudgetPeriod period,
            Instant createdAt,
            Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.category = Objects.requireNonNull(category);
        this.limit = Objects.requireNonNull(limit);
        this.spent = Objects.requireNonNull(spent);
        this.period = Objects.requireNonNull(period);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
    }

    public static Budget create(
            UserId ownerId,
            Category category,
            Money limit,
            BudgetPeriod period) {
        if (limit.isNegative() || limit.isZero()) {
            throw new IllegalArgumentException("Budget limit must be positive");
        }
        Instant now = Instant.now();
        return new Budget(
                BudgetId.generate(),
                ownerId,
                category,
                limit,
                Money.zero(limit.getCurrency()),
                period,
                now,
                now
        );
    }

    public static Budget reconstitute(
            BudgetId id,
            UserId ownerId,
            Category category,
            Money limit,
            Money spent,
            BudgetPeriod period,
            Instant createdAt,
            Instant updatedAt) {
        return new Budget(id, ownerId, category, limit, spent, period, createdAt, updatedAt);
    }

    // --- Business behaviour ---

    public void recordSpending(Money amount) {
        Objects.requireNonNull(amount, "Amount must not be null");
        if (amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Spending amount must be positive");
        }

        this.spent = this.spent.add(amount);
        this.updatedAt = Instant.now();

        if (isExceeded()) {
            registerEvent(new BudgetExceededEvent(
                    this.id,
                    this.ownerId,
                    this.category,
                    this.limit,
                    this.spent,
                    this.period
            ));
        }
    }

    public void updateLimit(Money newLimit) {
        if (newLimit.isNegative() || newLimit.isZero()) {
            throw new IllegalArgumentException("Budget limit must be positive");
        }
        this.limit = newLimit;
        this.updatedAt = Instant.now();
    }

    public boolean isExceeded() {
        return spent.isGreaterThan(limit);
    }

    public Money getRemainingAmount() {
        return limit.subtract(spent);
    }

    public double getUsagePercentage() {
        if (limit.isZero()) return 0.0;
        return spent.getAmount()
                .divide(limit.getAmount(), 4, java.math.RoundingMode.HALF_EVEN)
                .multiply(java.math.BigDecimal.valueOf(100))
                .doubleValue();
    }

    public BudgetId getId() { return id; }
    public UserId getOwnerId() { return ownerId; }
    public Category getCategory() { return category; }
    public Money getLimit() { return limit; }
    public Money getSpent() { return spent; }
    public BudgetPeriod getPeriod() { return period; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}