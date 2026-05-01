package com.fintrack.adapter.out.persistence.mapper;

import com.fintrack.adapter.out.persistence.entity.BudgetJpaEntity;
import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetId;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.Category;
import com.fintrack.domain.model.user.UserId;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Currency;

@Component
public class BudgetPersistenceMapper {

    public BudgetJpaEntity toJpaEntity(Budget budget) {
        return new BudgetJpaEntity(
                budget.getId().value(),
                budget.getOwnerId().value(),
                budget.getCategory().name(),
                budget.getLimit().getAmount(),
                budget.getLimit().getCurrency().getCurrencyCode(),
                budget.getSpent().getAmount(),
                budget.getSpent().getCurrency().getCurrencyCode(),
                budget.getPeriod().yearMonth().getYear(),
                budget.getPeriod().yearMonth().getMonthValue(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }

    public Budget toDomain(BudgetJpaEntity entity) {
        return Budget.reconstitute(
                new BudgetId(entity.getId()),
                new UserId(entity.getOwnerId()),
                Category.valueOf(entity.getCategory()),
                Money.of(entity.getLimitAmount(), Currency.getInstance(entity.getLimitCurrency())),
                Money.of(entity.getSpentAmount(), Currency.getInstance(entity.getSpentCurrency())),
                new BudgetPeriod(YearMonth.of(entity.getPeriodYear(), entity.getPeriodMonth())),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}