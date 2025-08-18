create TABLE
    product_statistic (
        product_statistic_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        order_id BIGINT NOT NULL,
        product_id BIGINT NOT NULL,
        purchase_count INTEGER NOT NULL,

        CONSTRAINT fk_product_statistic_order FOREIGN KEY (order_id) REFERENCES "order"(order_id),
        CONSTRAINT fk_product_statistic_product FOREIGN KEY (product_id) REFERENCES product(product_id)
    );

comment on table product_statistic is 'Таблица статистики проданных товаров';
comment on column product_statistic.product_statistic_id is 'ID записи';
comment on column product_statistic.order_id is 'ID заказа';
comment on column product_statistic.product_id is 'ID товара';
comment on column product_statistic.purchase_count is 'Количество проданного товара';