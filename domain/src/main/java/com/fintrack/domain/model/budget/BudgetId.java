package com.fintrack.domain.model.budget;

import java.util.Objects;
import java.util.UUID;

public record BudgetId(UUID value) {

    public BudgetId {
        Objects.requireNonNull(value, "BudgetId must not be null");
    }

    public static BudgetId generate() {
        return new BudgetId(UUID.randomUUID());
    }

    public static BudgetId of(String value) {
        return new BudgetId(UUID.fromString(value));
    }

    @Override
    public String toString() { return value.toString(); }
}