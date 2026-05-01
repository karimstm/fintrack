package com.fintrack.domain.model.budget;

import java.time.YearMonth;
import java.util.Objects;

public record BudgetPeriod(YearMonth yearMonth) {

    public BudgetPeriod {
        Objects.requireNonNull(yearMonth, "YearMonth must not be null");
    }

    public static BudgetPeriod of(int year, int month) {
        return new BudgetPeriod(YearMonth.of(year, month));
    }

    public static BudgetPeriod current() {
        return new BudgetPeriod(YearMonth.now());
    }

    public boolean isCurrent() {
        return yearMonth.equals(YearMonth.now());
    }

    @Override
    public String toString() { return yearMonth.toString(); }
}