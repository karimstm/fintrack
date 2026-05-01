package com.fintrack.domain.model.account;

import com.fintrack.domain.event.TransactionRecordedEvent;
import com.fintrack.domain.exception.InsufficientFundsException;
import com.fintrack.domain.model.shared.AggregateRoot;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.Transaction;
import com.fintrack.domain.model.transaction.TransactionType;
import com.fintrack.domain.model.user.UserId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Account extends AggregateRoot {

    private final AccountId id;
    private final UserId ownerId;
    private final AccountType type;
    private String name;
    private Money balance;
    private AccountStatus status;
    private final Instant createdAt;
    private Instant updatedAt;
    private final List<Transaction> transactions;

    // Private constructor — use factory methods
    private Account(
            AccountId id,
            UserId ownerId,
            AccountType type,
            String name,
            Money balance,
            AccountStatus status,
            Instant createdAt,
            Instant updatedAt) {
        this.id = Objects.requireNonNull(id);
        this.ownerId = Objects.requireNonNull(ownerId);
        this.type = Objects.requireNonNull(type);
        this.name = validateName(name);
        this.balance = Objects.requireNonNull(balance);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = Objects.requireNonNull(updatedAt);
        this.transactions = new ArrayList<>();
    }

    // Factory method for creating a brand-new account
    public static Account open(
            UserId ownerId,
            AccountType type,
            String name,
            Money initialBalance) {
        Instant now = Instant.now();
        return new Account(
                AccountId.generate(),
                ownerId,
                type,
                name,
                initialBalance,
                AccountStatus.ACTIVE,
                now,
                now
        );
    }

    // Factory method for reconstituting from persistence
    public static Account reconstitute(
            AccountId id,
            UserId ownerId,
            AccountType type,
            String name,
            Money balance,
            AccountStatus status,
            Instant createdAt,
            Instant updatedAt) {
        return new Account(id, ownerId, type, name, balance, status, createdAt, updatedAt);
    }

    // --- Business behaviour ---

    public Transaction credit(Money amount, String description, String category) {
        assertActive();
        Objects.requireNonNull(amount, "Amount must not be null");
        if (amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Credit amount must be positive");
        }

        this.balance = this.balance.add(amount);
        this.updatedAt = Instant.now();

        Transaction tx = Transaction.createCredit(this.id, amount, description, category);
        this.transactions.add(tx);

        registerEvent(new TransactionRecordedEvent(this.id, tx.getId(), amount, TransactionType.CREDIT));
        return tx;
    }

    public Transaction debit(Money amount, String description, String category) {
        assertActive();
        Objects.requireNonNull(amount, "Amount must not be null");
        if (amount.isNegative() || amount.isZero()) {
            throw new IllegalArgumentException("Debit amount must be positive");
        }
        if (type != AccountType.CREDIT_CARD && balance.subtract(amount).isNegative()) {
            throw new InsufficientFundsException(id, balance, amount);
        }

        this.balance = this.balance.subtract(amount);
        this.updatedAt = Instant.now();

        Transaction tx = Transaction.createDebit(this.id, amount, description, category);
        this.transactions.add(tx);

        registerEvent(new TransactionRecordedEvent(this.id, tx.getId(), amount, TransactionType.DEBIT));
        return tx;
    }

    public void rename(String newName) {
        this.name = validateName(newName);
        this.updatedAt = Instant.now();
    }

    public void freeze() {
        if (status == AccountStatus.CLOSED) {
            throw new IllegalStateException("Cannot freeze a closed account");
        }
        this.status = AccountStatus.FROZEN;
        this.updatedAt = Instant.now();
    }

    public void close() {
        if (!balance.isZero()) {
            throw new IllegalStateException(
                "Cannot close account with non-zero balance: " + balance
            );
        }
        this.status = AccountStatus.CLOSED;
        this.updatedAt = Instant.now();
    }

    // --- Guards ---

    private void assertActive() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException(
                "Account %s is not active (status: %s)".formatted(id, status)
            );
        }
    }

    private static String validateName(String name) {
        Objects.requireNonNull(name, "Account name must not be null");
        String trimmed = name.trim();
        if (trimmed.isBlank()) throw new IllegalArgumentException("Account name must not be blank");
        if (trimmed.length() > 100) throw new IllegalArgumentException("Account name too long (max 100 chars)");
        return trimmed;
    }

    // --- Getters (read-only access, no setters) ---

    public AccountId getId() { return id; }
    public UserId getOwnerId() { return ownerId; }
    public AccountType getType() { return type; }
    public String getName() { return name; }
    public Money getBalance() { return balance; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public List<Transaction> getTransactions() { return Collections.unmodifiableList(transactions); }
}