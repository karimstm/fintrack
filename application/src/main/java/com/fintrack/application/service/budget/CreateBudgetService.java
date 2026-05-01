package com.fintrack.application.service.budget;

import com.fintrack.application.port.in.budget.CreateBudgetUseCase;
import com.fintrack.application.port.out.budget.LoadBudgetPort;
import com.fintrack.application.port.out.budget.SaveBudgetPort;
import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;

public class CreateBudgetService implements CreateBudgetUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateBudgetService.class);

    private final SaveBudgetPort saveBudgetPort;
    private final LoadBudgetPort loadBudgetPort;

    public CreateBudgetService(SaveBudgetPort saveBudgetPort, LoadBudgetPort loadBudgetPort) {
        this.saveBudgetPort = saveBudgetPort;
        this.loadBudgetPort = loadBudgetPort;
    }

    @Override
    public Budget createBudget(Command command) {
        log.info("Creating budget for owner={} category={} period={}",
                command.ownerId(), command.category(), command.period());

        // Enforce one budget per category per period per user
        boolean alreadyExists = loadBudgetPort
                .loadByOwnerCategoryAndPeriod(command.ownerId(), command.category(), command.period())
                .isPresent();

        if (alreadyExists) {
            throw new IllegalStateException(
                "Budget already exists for category=%s period=%s"
                    .formatted(command.category(), command.period())
            );
        }

        Money limit = Money.of(command.limitAmount(), Currency.getInstance(command.currency()));

        Budget budget = Budget.create(
                command.ownerId(),
                command.category(),
                limit,
                command.period()
        );

        Budget saved = saveBudgetPort.save(budget);
        log.info("Budget created successfully id={}", saved.getId());
        return saved;
    }
}