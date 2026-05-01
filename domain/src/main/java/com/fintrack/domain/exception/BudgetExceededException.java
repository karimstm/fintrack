package com.fintrack.domain.exception;

import com.fintrack.domain.model.budget.BudgetId;
import com.fintrack.domain.model.shared.Money;

public class BudgetExceededException extends DomainException {

    public BudgetExceededException(BudgetId budgetId, Money limit, Money spent) {
        super("Budget %s exceeded: limit is %s but spent %s"
                .formatted(budgetId, limit, spent));
    }
}