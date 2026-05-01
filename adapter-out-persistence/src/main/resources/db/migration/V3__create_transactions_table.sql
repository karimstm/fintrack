CREATE TABLE transactions (
    id              UUID            PRIMARY KEY,
    account_id      UUID            NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    type            VARCHAR(10)     NOT NULL,
    amount          NUMERIC(19, 2)  NOT NULL,
    currency        VARCHAR(3)      NOT NULL,
    description     VARCHAR(255)    NOT NULL,
    category        VARCHAR(50)     NOT NULL,
    occurred_at     TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_transactions_account_id  ON transactions(account_id);
CREATE INDEX idx_transactions_occurred_at ON transactions(occurred_at DESC);
CREATE INDEX idx_transactions_category    ON transactions(category);