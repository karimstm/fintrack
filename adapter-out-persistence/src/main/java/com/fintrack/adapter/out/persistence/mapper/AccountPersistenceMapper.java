package com.fintrack.adapter.out.persistence.mapper;

import com.fintrack.adapter.out.persistence.entity.AccountJpaEntity;
import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.account.AccountStatus;
import com.fintrack.domain.model.account.AccountType;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.user.UserId;
import org.springframework.stereotype.Component;

import java.util.Currency;

@Component
public class AccountPersistenceMapper {

    public AccountJpaEntity toJpaEntity(Account account) {
        return new AccountJpaEntity(
                account.getId().value(),
                account.getOwnerId().value(),
                account.getType().name(),
                account.getName(),
                account.getBalance().getAmount(),
                account.getBalance().getCurrency().getCurrencyCode(),
                account.getStatus().name(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    public Account toDomain(AccountJpaEntity entity) {
        return Account.reconstitute(
                new AccountId(entity.getId()),
                new UserId(entity.getOwnerId()),
                AccountType.valueOf(entity.getType()),
                entity.getName(),
                Money.of(entity.getBalanceAmount(), Currency.getInstance(entity.getBalanceCurrency())),
                AccountStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}