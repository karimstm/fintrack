package com.fintrack.application.port.out.transaction;

import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.transaction.Transaction;
import com.fintrack.domain.model.transaction.TransactionId;
import com.fintrack.domain.model.user.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LoadTransactionPort {

    Optional<Transaction> loadById(TransactionId transactionId);

    List<Transaction> loadByAccount(AccountId accountId, UserId ownerId);

    List<Transaction> loadByAccountAndDateRange(
            AccountId accountId,
            UserId ownerId,
            Instant from,
            Instant to
    );
}