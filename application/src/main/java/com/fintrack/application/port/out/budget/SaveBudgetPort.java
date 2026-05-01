package com.fintrack.application.port.out.budget;

import com.fintrack.domain.model.budget.Budget;

public interface SaveBudgetPort {

    Budget save(Budget budget);
}