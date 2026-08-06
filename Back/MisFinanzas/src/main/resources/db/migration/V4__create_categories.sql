CREATE TABLE categories (
    id    BIGSERIAL    PRIMARY KEY,
    name  VARCHAR(100) NOT NULL,
    type  VARCHAR(20)  NOT NULL,
    CONSTRAINT uq_categories_type_name UNIQUE (type, name)
);

INSERT INTO categories (name, type) VALUES
    ('Salario', 'INCOME'),
    ('Freelance', 'INCOME'),
    ('Inversiones', 'INCOME'),
    ('Negocio', 'INCOME'),
    ('Intereses', 'INCOME'),
    ('Regalos', 'INCOME'),
    ('Otros ingresos', 'INCOME'),
    ('Alimentación', 'EXPENSE'),
    ('Transporte', 'EXPENSE'),
    ('Vivienda', 'EXPENSE'),
    ('Servicios', 'EXPENSE'),
    ('Salud', 'EXPENSE'),
    ('Educación', 'EXPENSE'),
    ('Entretenimiento', 'EXPENSE'),
    ('Compras', 'EXPENSE'),
    ('Otros gastos', 'EXPENSE');

ALTER TABLE incomes ADD COLUMN category_id BIGINT;

UPDATE incomes SET category_id = c.id
FROM categories c
WHERE c.type = 'INCOME' AND LOWER(c.name) = LOWER(incomes.category);

UPDATE incomes SET category_id = c.id
FROM categories c
WHERE incomes.category_id IS NULL AND c.type = 'INCOME' AND c.name = 'Otros ingresos';

ALTER TABLE incomes ALTER COLUMN category_id SET NOT NULL;
ALTER TABLE incomes ADD CONSTRAINT fk_incomes_category FOREIGN KEY (category_id) REFERENCES categories (id);
ALTER TABLE incomes DROP COLUMN category;

CREATE INDEX idx_incomes_category_id ON incomes (category_id);

ALTER TABLE expenses ADD COLUMN category_id BIGINT;

UPDATE expenses SET category_id = c.id
FROM categories c
WHERE c.type = 'EXPENSE' AND LOWER(c.name) = LOWER(expenses.category);

UPDATE expenses SET category_id = c.id
FROM categories c
WHERE expenses.category_id IS NULL AND c.type = 'EXPENSE' AND c.name = 'Otros gastos';

ALTER TABLE expenses ALTER COLUMN category_id SET NOT NULL;
ALTER TABLE expenses ADD CONSTRAINT fk_expenses_category FOREIGN KEY (category_id) REFERENCES categories (id);
ALTER TABLE expenses DROP COLUMN category;

CREATE INDEX idx_expenses_category_id ON expenses (category_id);
