UPDATE credit_card_purchases p
SET billing_cycle =
    CASE
        WHEN p.purchase_date > LEAST(
                 date_trunc('month', p.purchase_date)::date + (c.cutoff_day - 1),
                 (date_trunc('month', p.purchase_date) + interval '1 month - 1 day')::date)
            THEN LEAST(
                 (date_trunc('month', p.purchase_date) + interval '1 month')::date + (c.cutoff_day - 1),
                 (date_trunc('month', p.purchase_date) + interval '2 months - 1 day')::date)
        ELSE LEAST(
                 date_trunc('month', p.purchase_date)::date + (c.cutoff_day - 1),
                 (date_trunc('month', p.purchase_date) + interval '1 month - 1 day')::date)
    END
FROM credit_cards c
WHERE p.credit_card_id = c.id
  AND p.billing_cycle IS NULL
  AND p.deleted = false;