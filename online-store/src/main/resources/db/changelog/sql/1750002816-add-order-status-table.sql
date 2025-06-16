create TABLE order_status (
    order_status_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    order_status_name VARCHAR(100) NOT NULL UNIQUE,
    order_status_description VARCHAR(255)
);

comment on table order_status is 'Статусы доставки';
comment on column order_status.order_status_id is 'Идентификатор статуса доставки';
comment on column order_status.order_status_name is 'Наименование статуса доставки';
comment on column order_status.order_status_description is 'Описание статуса доставки';