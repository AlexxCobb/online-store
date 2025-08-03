create TABLE
    payment_method (
        payment_method_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        payment_method_name VARCHAR(100) NOT NULL UNIQUE,
        payment_method_description VARCHAR(255)
    );

comment on table payment_method is 'Способы оплаты';
comment on column payment_method.payment_method_id is 'Идентификатор способа оплаты';
comment on column payment_method.payment_method_name is 'Наименование метода оплаты';
comment on column payment_method.payment_method_description is 'Описание метода оплаты';