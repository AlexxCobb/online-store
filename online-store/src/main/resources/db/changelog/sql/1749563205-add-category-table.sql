CREATE TABLE "category" (
                        category_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        category_public_id VARCHAR(255) NOT NULL UNIQUE,
                        category_name VARCHAR(100) NOT NULL
);
comment on column "category".category_id is 'ID категории';
comment on column "category".category_public_id is 'Публичный ID категории для внешнего использования';
comment on column "category".category_name is 'Название категории в формате: "Планшеты", "Ноутбуки" и т.д.';