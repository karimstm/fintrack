package com.fintrack.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "budgets")
public class BudgetJpaEntity {

    @Id
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "limit_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "limit_currency", nullable = false, length = 3)
    private String limitCurrency;

    @Column(name = "spent_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal spentAmount;

    @Column(name = "spent_currency", nullable = false, length = 3)
    private String spentCurrency;

    @Column(name = "period_year", nullable = false)
    private int periodYear;

    @Column(name = "period_month", nullable = false)
    private int periodMonth;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BudgetJpaEntity() {}

    public BudgetJpaEntity(
            UUID id, UUID ownerId, String category,
            BigDecimal limitAmount, String limitCurrency,
            BigDecimal spentAmount, String spentCurrency,
            int periodYear, int periodMonth,
            Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.category = category;
        this.limitAmount = limitAmount;
        this.limitCurrency = limitCurrency;
        this.spentAmount = spentAmount;
        this.spentCurrency = spentCurrency;
        this.periodYear = periodYear;
        this.periodMonth = periodMonth;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getOwnerId() { return ownerId; }
    public String getCategory() { return category; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public String getLimitCurrency() { return limitCurrency; }
    public BigDecimal getSpentAmount() { return spentAmount; }
    public String getSpentCurrency() { return spentCurrency; }
    public int getPeriodYear() { return periodYear; }
    public int getPeriodMonth() { return periodMonth; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}