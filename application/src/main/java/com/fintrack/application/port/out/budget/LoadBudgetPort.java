package com.fintrack.application.port.out.budget;

import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetId;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.transaction.Category;
import com.fintrack.domain.model.user.UserId;

import java.util.List;
import java.util.Optional;

public interface LoadBudgetPort {

    Optional<Budget> loadById(BudgetId budgetId);

    Optional<Budget> loadByOwnerCategoryAndPeriod(
            UserId ownerId,
            Category category,
            BudgetPeriod period
    );

    List<Budget> loadAllByOwnerAndPeriod(UserId ownerId, BudgetPeriod period);

    List<Budget> loadAllByOwner(UserId ownerId);
}