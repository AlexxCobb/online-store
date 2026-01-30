create TABLE
    order_item (
        order_item_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        product_id BIGINT NOT NULL,
        order_id BIGINT NOT NULL,
        quantity INTEGER NOT NULL,
        price_at_purchase numeric(10,2) NOT NULL,

        CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product(product_id),
        CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES "order"(order_id)
    );

comment on table order_item is 'Таблица товаров, относящихся к заказу';
comment on column order_item.order_item_id is 'ID записи';
comment on column order_item.product_id is 'ID продукта';
comment on column order_item.order_id is 'ID заказа';
comment on column order_item.quantity is 'Количество заказанного товара';
comment on column order_item.price_at_purchase is 'Стоимость на момент оформления заказа';