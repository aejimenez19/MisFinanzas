ALTER TABLE credit_cards
    ADD COLUMN last_four_digits VARCHAR(4),
    ADD CONSTRAINT ck_credit_cards_last_four_digits
        CHECK (last_four_digits IS NULL OR last_four_digits ~ '^[0-9]{4}$');
