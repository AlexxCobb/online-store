create TABLE
    address_type (
        address_type_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        address_type_name VARCHAR(100) NOT NULL UNIQUE,
        address_type_description VARCHAR(255) NOT NULL
    );

comment on table address_type is 'Тип адреса';
comment on column address_type.address_type_id is 'ID типа адреса';
comment on column address_type.address_type_name is 'Наименование типа адреса';
comment on column address_type.address_type_description is 'Описание типа адреса';