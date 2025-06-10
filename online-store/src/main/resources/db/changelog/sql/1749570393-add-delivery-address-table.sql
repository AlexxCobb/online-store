create TABLE delivery_address (
    delivery_address_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    delivery_address_public_id UUID NOT NULL UNIQUE,
    country VARCHAR(100) NOT NULL,
    town VARCHAR(100) NOT NULL,
    zip_code INTEGER NOT NULL,
    street VARCHAR(255) NOT NULL,
    house_number INTEGER NOT NULL,
    flat_number INTEGER
);

comment on table delivery_address is 'Таблица для хранения адресов доставки';
comment on column delivery_address.delivery_address_id is 'ID адреса доставки';
comment on column delivery_address.delivery_address_public_id is 'Публичный ID адреса для внешнего использования';
comment on column delivery_address.country is 'Страна доставки (в формате ISO 3166 или полное название)';
comment on column delivery_address.town is 'Город/населенный пункт доставки';
comment on column delivery_address.zip_code is 'Почтовый индекс';
comment on column delivery_address.street is 'Улица, проспект, бульвар и т.д.';
comment on column delivery_address.house_number is 'Номер дома';
comment on column delivery_address.flat_number is 'Номер квартиры/офиса (опционально)';

create index idx_delivery_address_public_id on delivery_address (delivery_address_public_id);