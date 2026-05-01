package com.fintrack.application.port.in.budget;

import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetId;
import com.fintrack.domain.model.user.UserId;

import java.math.BigDecimal;
import java.util.Objects;

public interface UpdateBudgetUseCase {

    Budget updateLimit(Command command);

    record Command(
            BudgetId budgetId,
            UserId ownerId,
            BigDecimal newLimitAmount,
            String currency
    ) {
        public Command {
            Objects.requireNonNull(budgetId, "budgetId must not be null");
            Objects.requireNonNull(ownerId, "ownerId must not be null");
            Objects.requireNonNull(newLimitAmount, "newLimitAmount must not be null");
            Objects.requireNonNull(currency, "currency must not be null");
            if (newLimitAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("newLimitAmount must be positive");
            }
        }
    }
}