create TABLE
    "order" (
        order_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        order_public_id VARCHAR(255) NOT NULL UNIQUE,
        user_id BIGINT NOT NULL,
        delivery_address_id BIGINT NOT NULL,
        payment_method_id INTEGER NOT NULL,
        delivery_method_id INTEGER NOT NULL,
        payment_status_id INTEGER NOT NULL,
        order_status_id INTEGER NOT NULL,
        created_at TIMESTAMPTZ NOT NULL,
        updated_at TIMESTAMPTZ NOT NULL,

        CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES "user"(user_id),
        CONSTRAINT fk_order_address FOREIGN KEY (delivery_address_id) REFERENCES delivery_address(delivery_address_id),
        CONSTRAINT fk_order_payment_method FOREIGN KEY (payment_method_id) REFERENCES payment_method(payment_method_id),
        CONSTRAINT fk_order_delivery_method FOREIGN KEY (delivery_method_id) REFERENCES delivery_method(delivery_method_id),
        CONSTRAINT fk_order_payment_status FOREIGN KEY (payment_status_id) REFERENCES payment_status(payment_status_id),
        CONSTRAINT fk_order_order_status FOREIGN KEY (order_status_id) REFERENCES order_status(order_status_id)
    );

comment on table "order" is 'Таблица заказов';
comment on column "order".order_id is 'ID заказа';
comment on column "order".order_public_id is 'Публичный ID заказа для внешнего использования';
comment on column "order".user_id is 'ID пользователя, оформившего заказ';
comment on column "order".delivery_address_id is 'ID адреса доставки заказа';
comment on column "order".payment_method_id is 'ID способа оплаты';
comment on column "order".delivery_method_id is 'ID способа доставки';
comment on column "order".payment_status_id is 'ID статуса оплаты';
comment on column "order".order_status_id is 'ID статуса доставки';
comment on column "order".created_at is 'Дата и время создания заказа';
comment on column "order".updated_at is 'Дата и время последнего обновления заказа';

create index idx_order_public_id on "order" (order_public_id);
create index idx_order_user_id on "order" (user_id);