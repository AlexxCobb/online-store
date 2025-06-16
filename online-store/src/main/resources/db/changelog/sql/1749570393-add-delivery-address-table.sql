create TABLE
    delivery_address (
        delivery_address_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
        delivery_address_public_id VARCHAR(255) NOT NULL UNIQUE,
        user_id BIGINT,
        address_type_id INTEGER NOT NULL,
        country VARCHAR(100) NOT NULL,
        town VARCHAR(100) NOT NULL,
        zip_code INTEGER NOT NULL,
        street VARCHAR(255) NOT NULL,
        house_number INTEGER NOT NULL,
        flat_number INTEGER,
        is_active BOOLEAN NOT NULL,
        is_system BOOLEAN NOT NULL,

        CONSTRAINT fk_delivery_address_user FOREIGN KEY (user_id) REFERENCES "user"(user_id),
        CONSTRAINT fk_delivery_address_address_type FOREIGN KEY (address_type_id) REFERENCES address_type(address_type_id)
);

comment on table delivery_address is 'Таблица для хранения адресов доставки';
comment on column delivery_address.delivery_address_id is 'ID адреса доставки';
comment on column delivery_address.delivery_address_public_id is 'Публичный ID адреса для внешнего использования';
comment on column delivery_address.user_id is 'ID пользователя';
comment on column delivery_address.address_type_id is 'ID типа адреса';
comment on column delivery_address.country is 'Страна доставки (в формате ISO 3166 или полное название)';
comment on column delivery_address.town is 'Город/населенный пункт доставки';
comment on column delivery_address.zip_code is 'Почтовый индекс';
comment on column delivery_address.street is 'Улица, проспект, бульвар и т.д.';
comment on column delivery_address.house_number is 'Номер дома';
comment on column delivery_address.flat_number is 'Номер квартиры/офиса (опционально)';
comment on column delivery_address.is_active is 'Адрес актуальный или нет (для магазина/ПВЗ)';
comment on column delivery_address.is_system is 'true для ПВЗ/магазинов)';

create index idx_delivery_address_public_id on delivery_address (delivery_address_public_id);