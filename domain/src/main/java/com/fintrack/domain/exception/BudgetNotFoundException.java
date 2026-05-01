package com.fintrack.domain.exception;

import com.fintrack.domain.model.budget.BudgetId;

public class BudgetNotFoundException extends DomainException {

    public BudgetNotFoundException(BudgetId id) {
        super("Budget not found: " + id);
    }
}