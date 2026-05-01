package com.fintrack.application.port.in.transaction;

import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.transaction.Transaction;
import com.fintrack.domain.model.transaction.TransactionType;
import com.fintrack.domain.model.user.UserId;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public interface RecordTransactionUseCase {

    Transaction recordTransaction(Command command);

    List<Transaction> getTransactions(AccountId accountId, UserId ownerId);

    record Command(
            AccountId accountId,
            UserId ownerId,
            TransactionType type,
            BigDecimal amount,
            String currency,
            String description,
            String category
    ) {
        public Command {
            Objects.requireNonNull(accountId, "accountId must not be null");
            Objects.requireNonNull(ownerId, "ownerId must not be null");
            Objects.requireNonNull(type, "type must not be null");
            Objects.requireNonNull(amount, "amount must not be null");
            Objects.requireNonNull(currency, "currency must not be null");
            Objects.requireNonNull(description, "description must not be null");
            Objects.requireNonNull(category, "category must not be null");
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("amount must be positive");
            }
            if (description.isBlank()) {
                throw new IllegalArgumentException("description must not be blank");
            }
        }
    }
}