CREATE TABLE product (
                        product_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        product_public_id VARCHAR(255) NOT NULL UNIQUE,
                        product_name VARCHAR(255) NOT NULL,
                        product_price numeric(10,2) NOT NULL,
                        category_id INTEGER NOT NULL,
                        product_weight numeric(10,2) NOT NULL,
                        product_volume numeric(10,2) NOT NULL,
                        product_stock_quantity INTEGER NOT NULL,

CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES "category"(category_id)
);

comment on column product.product_id is 'ID продукта';
comment on column product.product_public_id is 'Публичный ID продукта для внешнего использования';
comment on column product.product_name is 'Название продукта';
comment on column product.product_price is 'Цена товара';
comment on column product.category_id is 'ID категории';
comment on column product.product_weight is 'Вес товара в килограммах';
comment on column product.product_volume is 'Объем товара в метрах кубических';
comment on column product.product_stock_quantity is 'Количество товара на складе';

create index idx_product_public_id on product (product_public_id);