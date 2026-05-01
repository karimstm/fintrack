package com.fintrack.application.port.in.budget;

import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.transaction.Category;
import com.fintrack.domain.model.user.UserId;

import java.math.BigDecimal;
import java.util.Objects;

public interface CreateBudgetUseCase {

    Budget createBudget(Command command);

    record Command(
            UserId ownerId,
            Category category,
            BigDecimal limitAmount,
            String currency,
            BudgetPeriod period
    ) {
        public Command {
            Objects.requireNonNull(ownerId, "ownerId must not be null");
            Objects.requireNonNull(category, "category must not be null");
            Objects.requireNonNull(limitAmount, "limitAmount must not be null");
            Objects.requireNonNull(currency, "currency must not be null");
            Objects.requireNonNull(period, "period must not be null");
            if (limitAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("limitAmount must be positive");
            }
        }
    }
}