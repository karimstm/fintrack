package com.fintrack.adapter.out.persistence.mapper;

import com.fintrack.adapter.out.persistence.entity.TransactionJpaEntity;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.Category;
import com.fintrack.domain.model.transaction.Transaction;
import com.fintrack.domain.model.transaction.TransactionId;
import com.fintrack.domain.model.transaction.TransactionType;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class TransactionPersistenceMapper {

    public TransactionJpaEntity toJpaEntity(Transaction transaction) {
        return new TransactionJpaEntity(
                transaction.getId().value(),
                transaction.getAccountId().value(),
                transaction.getType().name(),
                transaction.getAmount().getAmount(),
                transaction.getAmount().getCurrency().getCurrencyCode(),
                transaction.getDescription(),
                transaction.getCategory().name(),
                transaction.getOccurredAt()
        );
    }

    public Transaction toDomain(TransactionJpaEntity entity) {
        return Transaction.reconstitute(
                new TransactionId(entity.getId()),
                new AccountId(entity.getAccountId()),
                TransactionType.valueOf(entity.getType()),
                Money.of(entity.getAmount(), Currency.getInstance(entity.getCurrency())),
                entity.getDescription(),
                Category.valueOf(entity.getCategory()),
                entity.getOccurredAt()
        );
    }
}