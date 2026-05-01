CREATE TABLE budgets (
    id               UUID            PRIMARY KEY,
    owner_id         UUID            NOT NULL,
    category         VARCHAR(50)     NOT NULL,
    limit_amount     NUMERIC(19, 2)  NOT NULL,
    limit_currency   VARCHAR(3)      NOT NULL,
    spent_amount     NUMERIC(19, 2)  NOT NULL DEFAULT 0.00,
    spent_currency   VARCHAR(3)      NOT NULL,
    period_year      SMALLINT        NOT NULL,
    period_month     SMALLINT        NOT NULL,
    created_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),

    CONSTRAINT uq_budget_owner_category_period
        UNIQUE (owner_id, category, period_year, period_month)
);

CREATE INDEX idx_budgets_owner_id ON budgets(owner_id);
CREATE INDEX idx_budgets_period   ON budgets(period_year, period_month);