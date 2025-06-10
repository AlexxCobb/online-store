create TABLE "order" (
                        order_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
                        order_public_id VARCHAR(255) NOT NULL UNIQUE,
                        user_id BIGINT NOT NULL,
                        delivery_address_id BIGINT NOT NULL,
                        payment_method VARCHAR(20) NOT NULL,
                        delivery_method VARCHAR(20) NOT NULL,
                        product_id BIGINT NOT NULL,
                        payment_status VARCHAR(20) NOT NULL,
                        delivery_status VARCHAR(20) NOT NULL,
                        created_at TIMESTAMPTZ NOT NULL,
                        updated_at TIMESTAMPTZ NOT NULL,

CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES "user"(user_id),

CONSTRAINT fk_order_address FOREIGN KEY (delivery_address_id) REFERENCES delivery_address(delivery_address_id),

CONSTRAINT fk_order_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);

comment on table "order" is 'Таблица заказов';
comment on column "order".order_id is 'ID заказа';
comment on column "order".order_public_id is 'Публичный ID заказа для внешнего использования';
comment on column "order".user_id is 'ID пользователя, оформившего заказ';
comment on column "order".delivery_address_id is 'ID адреса доставки заказа';
comment on column "order".payment_method is 'Способ оплаты (card, cash, etc)';
comment on column "order".delivery_method is 'Способ доставки (courier, pickup, etc)';
comment on column "order".product_id is 'ID заказанног товара';
comment on column "order".payment_status is 'Текущий статус оплаты (pending, completed, failed)';
comment on column "order".delivery_status is 'Текущий статус доставки (processing, shipped, delivered)';
comment on column "order".created_at is 'Дата и время создания заказа';
comment on column "order".updated_at is 'Дата и время последнего обновления заказа';

CREATE INDEX idx_order_public_id ON "order" (order_public_id);
CREATE INDEX idx_order_user_id ON "order" (user_id);
CREATE INDEX idx_order_product_id ON "order" (product_id);
CREATE INDEX idx_order_delivery_status ON "order" (delivery_status);