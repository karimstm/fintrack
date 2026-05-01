package com.fintrack.application.service.budget;

import com.fintrack.application.port.in.budget.UpdateBudgetUseCase;
import com.fintrack.application.port.out.budget.LoadBudgetPort;
import com.fintrack.application.port.out.budget.SaveBudgetPort;
import com.fintrack.domain.exception.BudgetNotFoundException;
import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.shared.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;

public class UpdateBudgetService implements UpdateBudgetUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateBudgetService.class);

    private final LoadBudgetPort loadBudgetPort;
    private final SaveBudgetPort saveBudgetPort;

    public UpdateBudgetService(LoadBudgetPort loadBudgetPort, SaveBudgetPort saveBudgetPort) {
        this.loadBudgetPort = loadBudgetPort;
        this.saveBudgetPort = saveBudgetPort;
    }

    @Override
    public Budget updateLimit(Command command) {
        log.info("Updating budget limit id={} owner={}", command.budgetId(), command.ownerId());

        Budget budget = loadBudgetPort.loadById(command.budgetId())
                .filter(b -> b.getOwnerId().equals(command.ownerId()))
                .orElseThrow(() -> new BudgetNotFoundException(command.budgetId()));

        Money newLimit = Money.of(command.newLimitAmount(), Currency.getInstance(command.currency()));
        budget.updateLimit(newLimit);

        Budget saved = saveBudgetPort.save(budget);
        log.info("Budget limit updated successfully id={}", saved.getId());
        return saved;
    }
}