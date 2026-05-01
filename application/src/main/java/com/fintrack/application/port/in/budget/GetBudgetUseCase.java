package com.fintrack.application.port.in.budget;

import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetId;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.user.UserId;

import java.util.List;

public interface GetBudgetUseCase {

    Budget getBudget(BudgetId budgetId, UserId ownerId);

    List<Budget> getBudgetsForPeriod(UserId ownerId, BudgetPeriod period);
}