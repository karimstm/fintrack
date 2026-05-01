-- Remove REFERENCES users(id) for now, add it back in Step 9 when User entity is ready
CREATE TABLE accounts (
    id               UUID            PRIMARY KEY,
    owner_id         UUID            NOT NULL,
    type             VARCHAR(50)     NOT NULL,
    name             VARCHAR(100)    NOT NULL,
    balance_amount   NUMERIC(19, 2)  NOT NULL DEFAULT 0.00,
    balance_currency VARCHAR(3)      NOT NULL,
    status           VARCHAR(50)     NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_accounts_owner_id ON accounts(owner_id);
CREATE INDEX idx_accounts_status   ON accounts(status);