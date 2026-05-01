package com.fintrack.domain.model.transaction;

import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.shared.Money;

import java.time.Instant;
import java.util.Objects;

public class Transaction {

    private final TransactionId id;
    private final AccountId accountId;
    private final TransactionType type;
    private final Money amount;
    private final String description;
    private final Category category;
    private final Instant occurredAt;

    private Transaction(
            TransactionId id,
            AccountId accountId,
            TransactionType type,
            Money amount,
            String description,
            Category category,
            Instant occurredAt) {
        this.id = Objects.requireNonNull(id);
        this.accountId = Objects.requireNonNull(accountId);
        this.type = Objects.requireNonNull(type);
        this.amount = Objects.requireNonNull(amount);
        this.description = Objects.requireNonNull(description);
        this.category = Objects.requireNonNull(category);
        this.occurredAt = Objects.requireNonNull(occurredAt);
    }

    public static Transaction createCredit(
            AccountId accountId,
            Money amount,
            String description,
            String categoryName) {
        return new Transaction(
                TransactionId.generate(),
                accountId,
                TransactionType.CREDIT,
                amount,
                description,
                parseCategory(categoryName),
                Instant.now()
        );
    }

    public static Transaction createDebit(
            AccountId accountId,
            Money amount,
            String description,
            String categoryName) {
        return new Transaction(
                TransactionId.generate(),
                accountId,
                TransactionType.DEBIT,
                amount,
                description,
                parseCategory(categoryName),
                Instant.now()
        );
    }

    // Reconstitute from persistence
    public static Transaction reconstitute(
            TransactionId id,
            AccountId accountId,
            TransactionType type,
            Money amount,
            String description,
            Category category,
            Instant occurredAt) {
        return new Transaction(id, accountId, type, amount, description, category, occurredAt);
    }

    private static Category parseCategory(String name) {
        try {
            return Category.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Category.OTHER;
        }
    }

    public TransactionId getId() { return id; }
    public AccountId getAccountId() { return accountId; }
    public TransactionType getType() { return type; }
    public Money getAmount() { return amount; }
    public String getDescription() { return description; }
    public Category getCategory() { return category; }
    public Instant getOccurredAt() { return occurredAt; }
}