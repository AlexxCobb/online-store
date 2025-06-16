create TABLE payment_status (
    payment_status_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    payment_status_name VARCHAR(100) NOT NULL UNIQUE,
    payment_status_description VARCHAR(255)
);

comment on table payment_status is 'Статусы оплаты';
comment on column payment_status.payment_status_id is 'Идентификатор статуса оплаты';
comment on column payment_status.payment_status_name is 'Наименование статуса оплаты';
comment on column payment_status.payment_status_description is 'Описание статуса оплаты';