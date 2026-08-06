CREATE TABLE users (
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255)   NOT NULL UNIQUE,
    password   VARCHAR(255)   NOT NULL,
    first_name VARCHAR(100)   NOT NULL,
    last_name  VARCHAR(100)   NOT NULL,
    role       VARCHAR(50)    NOT NULL DEFAULT 'USER',
    enabled    BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE incomes (
    id            BIGSERIAL     PRIMARY KEY,
    user_id       BIGINT        NOT NULL,
    description   VARCHAR(255)  NOT NULL,
    amount        NUMERIC(15,2) NOT NULL,
    category      VARCHAR(100)  NOT NULL,
    movement_date DATE          NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_incomes_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE expenses (
    id            BIGSERIAL     PRIMARY KEY,
    user_id       BIGINT        NOT NULL,
    description   VARCHAR(255)  NOT NULL,
    amount        NUMERIC(15,2) NOT NULL,
    category      VARCHAR(100)  NOT NULL,
    movement_date DATE          NOT NULL,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_expenses_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE credit_cards (
    id           BIGSERIAL     PRIMARY KEY,
    user_id      BIGINT        NOT NULL,
    name         VARCHAR(100)  NOT NULL,
    bank         VARCHAR(100)  NOT NULL,
    credit_limit NUMERIC(15,2) NOT NULL,
    cutoff_day   SMALLINT      NOT NULL,
    payment_day  SMALLINT      NOT NULL,
    status       VARCHAR(50)   NOT NULL DEFAULT 'ACTIVE',
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_credit_cards_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT ck_credit_cards_cutoff_day CHECK (cutoff_day BETWEEN 1 AND 31),
    CONSTRAINT ck_credit_cards_payment_day CHECK (payment_day BETWEEN 1 AND 31)
);

CREATE TABLE credit_card_purchases (
    id             BIGSERIAL     PRIMARY KEY,
    credit_card_id BIGINT        NOT NULL,
    description    VARCHAR(255)  NOT NULL,
    amount         NUMERIC(15,2) NOT NULL,
    purchase_date  DATE          NOT NULL,
    billing_cycle  DATE,
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted        BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_purchases_credit_card FOREIGN KEY (credit_card_id) REFERENCES credit_cards (id)
);

CREATE TABLE credit_card_payments (
    id             BIGSERIAL     PRIMARY KEY,
    credit_card_id BIGINT        NOT NULL,
    amount         NUMERIC(15,2) NOT NULL,
    payment_date   DATE          NOT NULL,
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted        BOOLEAN       NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_payments_credit_card FOREIGN KEY (credit_card_id) REFERENCES credit_cards (id)
);

CREATE INDEX idx_incomes_user_id ON incomes (user_id);
CREATE INDEX idx_expenses_user_id ON expenses (user_id);
CREATE INDEX idx_credit_cards_user_id ON credit_cards (user_id);
CREATE INDEX idx_purchases_credit_card_id ON credit_card_purchases (credit_card_id);
CREATE INDEX idx_payments_credit_card_id ON credit_card_payments (credit_card_id);
