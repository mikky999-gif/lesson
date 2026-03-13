-- Добавляем продукты
INSERT INTO products (id, name, type) VALUES
('147f6a0f-3b91-413b-ab99-87f081d60d5a', 'Invest 500', 'INVEST'),
('ab138afb-f3ba-4a93-b74f-0fcee86d447f', 'Простой кредит', 'CREDIT'),
('59efc529-2fff-41af-baff-90ccd7402925', 'Top Saving', 'SAVING'),
('debit-product', 'Дебетовый счёт', 'DEBIT');

-- Добавляем транзакции для тестовых пользователей
INSERT INTO transactions (user_id, product_id, amount, type) VALUES
-- Пользователь cd515076-... (должен получить Invest 500)
('cd515076-5d8a-44be-930e-8d4fcb79f42d', 'debit-product', 1500, 'DEPOSIT'),
('cd515076-5d8a-44be-930e-8d4fcb79f42d', '59efc529-2fff-41af-baff-90ccd7402925', 1200, 'DEPOSIT'),

-- Пользователь d4a4d619-... (должен получить Top Saving)
('d4a4d619-9a0c-4fc5-b0cb-76c49409546b', 'debit-product', 60000, 'DEPOSIT'),

-- Пользователь 1f9b149c-... (должен получить Simple Credit)
('1f9b149c-6577-448a-bc94-16bea229b71a', 'debit-product', 200000, 'WITHDRAWAL');