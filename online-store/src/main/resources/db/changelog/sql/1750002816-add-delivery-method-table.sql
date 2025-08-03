create TABLE
    delivery_method (
        delivery_method_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        delivery_method_name VARCHAR(100) NOT NULL UNIQUE,
        delivery_method_description VARCHAR(255)
    );

comment on table delivery_method is 'Способы доставки';
comment on column delivery_method.delivery_method_id is 'Идентификатор метода доставки';
comment on column delivery_method.delivery_method_name is 'Наименование метода доставки';
comment on column delivery_method.delivery_method_description is 'Описание метода доставки';