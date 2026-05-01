package com.fintrack.application.service.budget;

import com.fintrack.application.port.in.budget.GetBudgetUseCase;
import com.fintrack.application.port.out.budget.LoadBudgetPort;
import com.fintrack.domain.exception.BudgetNotFoundException;
import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetId;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GetBudgetService implements GetBudgetUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetBudgetService.class);

    private final LoadBudgetPort loadBudgetPort;

    public GetBudgetService(LoadBudgetPort loadBudgetPort) {
        this.loadBudgetPort = loadBudgetPort;
    }

    @Override
    public Budget getBudget(BudgetId budgetId, UserId ownerId) {
        log.debug("Loading budget id={} owner={}", budgetId, ownerId);
        return loadBudgetPort.loadById(budgetId)
                .filter(b -> b.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new BudgetNotFoundException(budgetId));
    }

    @Override
    public List<Budget> getBudgetsForPeriod(UserId ownerId, BudgetPeriod period) {
        log.debug("Loading budgets for owner={} period={}", ownerId, period);
        return loadBudgetPort.loadAllByOwnerAndPeriod(ownerId, period);
    }
}